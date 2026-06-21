package com.smartclinic.modules.opd.dto;

import com.smartclinic.modules.opd.domain.Prescription;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PrescriptionResponse(
        UUID                       id,
        String                     advice,
        Integer                    followUpDays,
        List<PrescriptionItemResponse> items,
        Instant                    createdAt
) {
    public static PrescriptionResponse from(Prescription p) {
        return new PrescriptionResponse(
                p.getId(),
                p.getAdvice(),
                p.getFollowUpDays(),
                p.getItems().stream().map(PrescriptionItemResponse::from).toList(),
                p.getCreatedAt()
        );
    }
}
