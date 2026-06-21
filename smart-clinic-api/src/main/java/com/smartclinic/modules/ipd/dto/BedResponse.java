package com.smartclinic.modules.ipd.dto;

import com.smartclinic.modules.ipd.domain.IpdBed;
import com.smartclinic.modules.ipd.domain.IpdBed.BedStatus;
import com.smartclinic.modules.ipd.domain.IpdBed.BedType;

import java.math.BigDecimal;
import java.util.UUID;

public record BedResponse(
        UUID       id,
        UUID       wardId,
        String     bedNumber,
        BedType    bedType,
        BigDecimal dailyCharge,
        BedStatus  status
) {
    public static BedResponse from(IpdBed b) {
        return new BedResponse(b.getId(), b.getWardId(), b.getBedNumber(),
                               b.getBedType(), b.getDailyCharge(), b.getStatus());
    }
}
