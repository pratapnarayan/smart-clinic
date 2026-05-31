package com.smarthospital.modules.opd.dto;

import com.smarthospital.modules.opd.domain.OpdCharge;

import java.math.BigDecimal;
import java.util.UUID;

public record OpdChargeResponse(
        UUID      id,
        String    description,
        BigDecimal amount,
        String    category
) {
    public static OpdChargeResponse from(OpdCharge c) {
        return new OpdChargeResponse(c.getId(), c.getDescription(), c.getAmount(), c.getCategory());
    }
}
