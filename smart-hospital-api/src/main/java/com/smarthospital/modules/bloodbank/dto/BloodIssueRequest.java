package com.smarthospital.modules.bloodbank.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BloodIssueRequest(
        @NotNull UUID requestId,
        @NotNull UUID unitId,
        String        issuedBy,
        String        notes
) {}
