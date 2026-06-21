package com.smartclinic.modules.setup.controller;

import com.smartclinic.modules.setup.dto.*;
import com.smartclinic.modules.setup.service.TenantProvisioningService;
import com.smartclinic.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/tenants")
@Tag(name = "Platform — Tenants", description = "Super-admin tenant provisioning and management")
public class TenantController {

    private final TenantProvisioningService provisioningService;

    public TenantController(TenantProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Provision a new hospital tenant",
               description = "Creates schema, runs all migrations, seeds admin user. Returns temporary credentials.")
    public ResponseEntity<ApiResponse<TenantProvisionedResponse>> provision(
            @Valid @RequestBody TenantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(provisioningService.provision(request)));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List all tenants")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(provisioningService.listTenants()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get tenant by ID")
    public ResponseEntity<ApiResponse<TenantResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(provisioningService.getTenant(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update tenant name, plan or status")
    public ResponseEntity<ApiResponse<TenantResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TenantUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(provisioningService.updateTenant(id, request)));
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Suspend a tenant (disables all logins for that hospital)")
    public ResponseEntity<ApiResponse<Void>> suspend(@PathVariable UUID id) {
        provisioningService.suspendTenant(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Reactivate a previously suspended tenant")
    public ResponseEntity<ApiResponse<Void>> reactivate(@PathVariable UUID id) {
        provisioningService.reactivateTenant(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
