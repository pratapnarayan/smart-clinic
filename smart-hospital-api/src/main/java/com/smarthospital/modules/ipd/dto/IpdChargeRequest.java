package com.smarthospital.modules.ipd.dto;

import com.smarthospital.modules.ipd.domain.IpdCharge.ChargeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IpdChargeRequest(
        @NotNull  ChargeCategory category,
        @NotBlank String         description,
        @NotNull @Positive BigDecimal amount,
        LocalDate               chargeDate
) {}
