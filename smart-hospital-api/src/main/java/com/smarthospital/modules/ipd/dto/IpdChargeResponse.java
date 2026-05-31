package com.smarthospital.modules.ipd.dto;

import com.smarthospital.modules.ipd.domain.IpdCharge;
import com.smarthospital.modules.ipd.domain.IpdCharge.ChargeCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record IpdChargeResponse(
        UUID           id,
        ChargeCategory category,
        String         description,
        BigDecimal     amount,
        LocalDate      chargeDate,
        Instant        createdAt
) {
    public static IpdChargeResponse from(IpdCharge c) {
        return new IpdChargeResponse(c.getId(), c.getCategory(),
                c.getDescription(), c.getAmount(), c.getChargeDate(), c.getCreatedAt());
    }
}
