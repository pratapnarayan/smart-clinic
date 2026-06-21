package com.smartclinic.core.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findBySchemaName(String schemaName);

    boolean existsBySchemaName(String schemaName);

    boolean existsByNameIgnoreCase(String name);

    List<Tenant> findByStatusOrderByNameAsc(String status);
}
