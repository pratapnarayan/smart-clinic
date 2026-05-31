package com.smarthospital.modules.radiology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record StudyRequest(
        @NotBlank String    code,
        @NotBlank String    name,
        @NotNull  UUID      modalityId,
        String              description,
        BigDecimal          price,
        String              prepInstructions
) {}
