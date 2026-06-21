package com.smartclinic.core.config;

import com.smartclinic.core.security.UserPrincipal;
import com.smartclinic.core.tenant.TenantAwareDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class JpaConfig {

    private final JpaProperties jpaProperties;

    @Value("${spring.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    public JpaConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    /**
     * Configures the JPA EntityManagerFactory to use a TenantAwareDataSource
     * wrapper that sets search_path on every connection based on TenantContext.
     *
     * The wrapper is NOT a Spring bean — it is created inline here so it never
     * appears in the context as a DataSource candidate, which would confuse
     * Flyway and JdbcTemplate auto-configuration.
     *
     * The auto-configured HikariCP 'dataSource' bean remains the primary
     * DataSource for Flyway, JdbcTemplate, Spring Security, etc.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        // Wrap HikariCP pool with search_path proxy — not a Spring bean
        TenantAwareDataSource tenantDs = new TenantAwareDataSource(dataSource);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(tenantDs);
        em.setPackagesToScan("com.smartclinic");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(jpaProperties.isShowSql());
        em.setJpaVendorAdapter(adapter);

        Map<String, Object> props = new HashMap<>(jpaProperties.getProperties());

        // Naming: camelCase Java fields → snake_case DB columns (Hibernate 6)
        props.put("hibernate.physical_naming_strategy",
                  "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        props.put("hibernate.implicit_naming_strategy",
                  "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");

        props.put(AvailableSettings.HBM2DDL_AUTO, ddlAuto);

        em.setJpaPropertyMap(props);
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                return Optional.of(principal.getUserId());
            }
            return Optional.of("system");
        };
    }
}
