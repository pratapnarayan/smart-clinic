package com.smartclinic.modules.setup.controller;

import com.smartclinic.core.tenant.TenantContext;
import com.smartclinic.core.tenant.TenantRepository;
import com.smartclinic.modules.setup.dto.TenantClinicConfigResponse;
import com.smartclinic.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant Config")
public class TenantConfigController {

    private final TenantRepository tenantRepository;

    public TenantConfigController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/current/config")
    @Operation(summary = "Get clinic configuration for the current tenant")
    public ResponseEntity<ApiResponse<TenantClinicConfigResponse>> getCurrentConfig() {
        String schemaName = TenantContext.get();
        if (schemaName == null || schemaName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return tenantRepository.findBySchemaName(schemaName)
                .map(t -> ResponseEntity.ok(ApiResponse.ok(new TenantClinicConfigResponse(t.getClinicType()))))
                .orElse(ResponseEntity.notFound().build());
    }
}
