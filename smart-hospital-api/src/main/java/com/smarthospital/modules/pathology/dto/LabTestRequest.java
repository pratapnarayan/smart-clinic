package com.smarthospital.modules.pathology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record LabTestRequest(
        @NotBlank String    code,
        @NotBlank String    name,
        @NotNull  UUID      categoryId,
        String              description,
        BigDecimal          price,
        Integer             turnaroundHours,
        String              unit,
        String              normalRange
) {}
