package com.smarthospital.modules.ipd.dto;

import com.smarthospital.modules.ipd.domain.IpdBed.BedType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BedCreateRequest(
        @NotBlank String     bedNumber,
        @NotNull  BedType    bedType,
        BigDecimal           dailyCharge
) {}
