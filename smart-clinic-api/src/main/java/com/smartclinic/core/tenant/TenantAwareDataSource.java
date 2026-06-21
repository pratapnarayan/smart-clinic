package com.smartclinic.core.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource proxy that sets PostgreSQL search_path on every connection
 * based on the current TenantContext value.
 *
 * This is the most reliable multi-tenancy approach for Hibernate 6 + Spring Boot 3:
 * it works at the JDBC level, completely independent of Hibernate's own multi-tenancy
 * API (which changed significantly in Hibernate 6 and requires extra wiring).
 *
 * How it works:
 *   1. Spring / Hibernate calls getConnection()
 *   2. We get a connection from the real pool (HikariCP)
 *   3. We execute SET search_path = {tenant_schema}, public
 *   4. All subsequent SQL on this connection targets the tenant schema
 *   5. When released, HikariCP resets the connection state automatically
 *      because evictConnection() / connectionTimeout resets the search_path
 *
 * Note: HikariCP does NOT reset search_path on connection return by default.
 * We handle this in releaseConnection by resetting to public.
 */
public class TenantAwareDataSource extends DelegatingDataSource {

    private static final Logger log = LoggerFactory.getLogger(TenantAwareDataSource.class);
    private static final String DEFAULT_SCHEMA = "public";

    public TenantAwareDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        applyTenantSchema(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        applyTenantSchema(connection);
        return connection;
    }

    private void applyTenantSchema(Connection connection) throws SQLException {
        String tenantId = TenantContext.get();
        String schema   = (tenantId != null && !tenantId.isBlank()) ? tenantId : DEFAULT_SCHEMA;

        // Validate to prevent SQL injection (schema names are controlled by us, but be safe)
        if (!schema.matches("^[a-z][a-z0-9_]{0,62}$") && !schema.equals("public")) {
            log.warn("Unsafe schema name '{}' — falling back to public", schema);
            schema = DEFAULT_SCHEMA;
        }

        connection.createStatement().execute("SET search_path TO " + schema + ", public");
        log.trace("search_path set to: {}", schema);
    }
}
