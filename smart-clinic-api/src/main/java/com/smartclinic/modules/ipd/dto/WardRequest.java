package com.smartclinic.modules.ipd.dto;

import com.smartclinic.modules.ipd.domain.IpdWard.WardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WardRequest(
        @NotBlank String   name,
        @NotNull  WardType wardType,
        @Min(0)   int      totalBeds
) {}
