package com.smartclinic.modules.pathology.dto;

import com.smartclinic.modules.pathology.domain.LabTest;

import java.math.BigDecimal;
import java.util.UUID;

public record LabTestResponse(
        UUID       id,
        String     code,
        String     name,
        UUID       categoryId,
        String     description,
        BigDecimal price,
        int        turnaroundHours,
        String     unit,
        String     normalRange,
        boolean    active
) {
    public static LabTestResponse from(LabTest t) {
        return new LabTestResponse(t.getId(), t.getCode(), t.getName(),
                t.getCategoryId(), t.getDescription(), t.getPrice(),
                t.getTurnaroundHours(), t.getUnit(), t.getNormalRange(), t.isActive());
    }
}
