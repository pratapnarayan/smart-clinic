package com.smartclinic.modules.setup.dto;

import jakarta.validation.constraints.Size;

public record TenantUpdateRequest(
        @Size(max = 200) String name,
        @Size(max = 50)  String plan,
        @Size(max = 20)  String status    // ACTIVE | SUSPENDED | INACTIVE
) {}
