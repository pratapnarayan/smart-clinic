package com.smartclinic.modules.ipd.dto;

import com.smartclinic.modules.ipd.domain.IpdWard;
import com.smartclinic.modules.ipd.domain.IpdWard.WardType;

import java.util.UUID;

public record WardResponse(
        UUID     id,
        String   name,
        WardType wardType,
        int      totalBeds,
        boolean  active
) {
    public static WardResponse from(IpdWard w) {
        return new WardResponse(w.getId(), w.getName(), w.getWardType(), w.getTotalBeds(), w.isActive());
    }
}
