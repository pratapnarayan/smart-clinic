package com.smarthospital.modules.radiology.dto;

import com.smarthospital.modules.radiology.domain.ImagingStudy;

import java.math.BigDecimal;
import java.util.UUID;

public record StudyResponse(
        UUID       id,
        String     code,
        String     name,
        UUID       modalityId,
        String     description,
        BigDecimal price,
        String     prepInstructions,
        boolean    active
) {
    public static StudyResponse from(ImagingStudy s) {
        return new StudyResponse(s.getId(), s.getCode(), s.getName(),
                s.getModalityId(), s.getDescription(), s.getPrice(),
                s.getPrepInstructions(), s.isActive());
    }
}
