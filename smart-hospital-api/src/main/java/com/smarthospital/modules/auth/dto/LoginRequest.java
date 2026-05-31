package com.smarthospital.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank        String password,

        /**
         * Optional tenant schema name, e.g. "hospital_001".
         * Required for tenant users (ADMIN, DOCTOR, etc.).
         * Omit for SUPER_ADMIN logins — they always resolve to the public schema.
         *
         * In production this is derived from the subdomain automatically by TenantFilter.
         * For dev/testing, pass it explicitly in the request body.
         */
        String tenantId
) {}
