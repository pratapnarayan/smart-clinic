package com.smarthospital.modules.ipd.dto;

import com.smarthospital.modules.ipd.domain.IpdWard.WardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WardRequest(
        @NotBlank String   name,
        @NotNull  WardType wardType,
        @Min(0)   int      totalBeds
) {}
