package com.smartclinic.modules.bloodbank.dto;

import com.smartclinic.modules.bloodbank.domain.BloodIssue;

import java.time.Instant;
import java.util.UUID;

public record BloodIssueResponse(
        UUID    id,
        String  issueNumber,
        Instant issueDate,
        UUID    requestId,
        String  requestNumber,
        UUID    unitId,
        String  unitNumber,
        String  bloodGroup,
        String  componentType,
        String  issuedTo,
        String  issuedBy,
        String  notes,
        Instant createdAt
) {
    public static BloodIssueResponse from(BloodIssue i) {
        return new BloodIssueResponse(
                i.getId(), i.getIssueNumber(), i.getIssueDate(),
                i.getRequestId(), i.getRequestNumber(),
                i.getUnitId(), i.getUnitNumber(),
                i.getBloodGroup(), i.getComponentType(),
                i.getIssuedTo(), i.getIssuedBy(),
                i.getNotes(), i.getCreatedAt()
        );
    }
}
