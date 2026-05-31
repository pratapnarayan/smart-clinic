package com.smarthospital.modules.radiology.dto;

import com.smarthospital.modules.radiology.domain.RadiologyOrderItem;
import com.smarthospital.modules.radiology.domain.RadiologyOrderItem.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RadiologyOrderItemResponse(
        UUID          id,
        UUID          studyId,
        String        studyCode,
        String        studyName,
        String        modalityName,
        BigDecimal    price,
        String        prepInstructions,
        ItemStatus    status,
        String        findings,
        String        impression,
        LocalDateTime reportedAt,
        String        reportedBy
) {
    public static RadiologyOrderItemResponse from(RadiologyOrderItem i) {
        return new RadiologyOrderItemResponse(
                i.getId(), i.getStudyId(), i.getStudyCode(), i.getStudyName(),
                i.getModalityName(), i.getPrice(), i.getPrepInstructions(),
                i.getStatus(), i.getFindings(), i.getImpression(),
                i.getReportedAt(), i.getReportedBy()
        );
    }
}
