package com.smarthospital.core.tenant;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * Runs Flyway migrations inside every tenant schema on startup.
 *
 * Execution order:
 *   Order(1) — this runs BEFORE DevDataSeeder (Order(2))
 *
 * How it works:
 *   - Flyway (Spring Boot auto-config) already ran V1__create_tenant_registry on "public".
 *   - This service reads all active tenant schemas from public.tenants,
 *     then runs the full migration set (V2+) inside each schema.
 *   - In Phase 1a dev, hospital_001 is inserted directly into public.tenants
 *     by this service itself if the table is empty (bootstrap mode).
 *
 * In production, tenants are created via TenantProvisioningService (Phase 1d).
 */
@Component
@Order(1)
public class TenantMigrationService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantMigrationService.class);

    private final DataSource dataSource;

    public TenantMigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> tenantSchemas = loadTenantSchemas();

        if (tenantSchemas.isEmpty()) {
            log.warn("[TenantMigration] No tenants found in public.tenants — skipping tenant migrations.");
            return;
        }

        for (String schema : tenantSchemas) {
            migrateTenantSchema(schema);
        }
    }

    /**
     * Migrate a single tenant schema.
     * Uses a dedicated Flyway instance pointing search_path at the tenant schema,
     * with a separate flyway_schema_history table per tenant so migration state is
     * isolated and doesn't interfere with public schema history.
     */
    public void migrateTenantSchema(String schemaName) {
        log.info("[TenantMigration] Running migrations for schema: {}", schemaName);
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .defaultSchema(schemaName)
                    .locations("classpath:db/migration")
                    .table("flyway_schema_history")           // per-schema history table
                    .baselineOnMigrate(true)
                    .outOfOrder(false)
                    .load();

            var result = flyway.migrate();
            log.info("[TenantMigration] Schema '{}' — {} migration(s) applied.", schemaName, result.migrationsExecuted);
        } catch (Exception e) {
            log.error("[TenantMigration] Failed to migrate schema '{}': {}", schemaName, e.getMessage(), e);
            throw new RuntimeException("Tenant migration failed for schema: " + schemaName, e);
        }
    }

    // ---------- private helpers ----------

    private List<String> loadTenantSchemas() {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            return jdbc.queryForList(
                    "SELECT schema_name FROM public.tenants WHERE status = 'ACTIVE' ORDER BY schema_name",
                    String.class);
        } catch (Exception e) {
            // public.tenants doesn't exist yet (very first run before V1 applied by Spring Flyway)
            log.warn("[TenantMigration] Could not read public.tenants — {}", e.getMessage());
            return List.of();
        }
    }
}
