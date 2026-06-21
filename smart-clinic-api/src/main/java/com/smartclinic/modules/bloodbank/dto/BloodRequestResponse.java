package com.smartclinic.modules.bloodbank.dto;

import com.smartclinic.modules.bloodbank.domain.BloodGroup;
import com.smartclinic.modules.bloodbank.domain.BloodRequest;
import com.smartclinic.modules.bloodbank.domain.BloodRequest.RequestStatus;
import com.smartclinic.modules.bloodbank.domain.BloodRequest.Urgency;
import com.smartclinic.modules.bloodbank.domain.ComponentType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BloodRequestResponse(
        UUID          id,
        String        requestNumber,
        LocalDate     requestDate,
        UUID          patientId,
        String        patientName,
        String        requestedBy,
        BloodGroup    bloodGroup,
        String        bloodGroupDisplay,
        ComponentType componentType,
        int           unitsRequired,
        int           unitsIssued,
        Urgency       urgency,
        RequestStatus status,
        LocalDateTime requiredBy,
        String        notes,
        Instant       createdAt
) {
    public static BloodRequestResponse from(BloodRequest r) {
        return new BloodRequestResponse(
                r.getId(), r.getRequestNumber(), r.getRequestDate(),
                r.getPatientId(), r.getPatientName(), r.getRequestedBy(),
                r.getBloodGroup(), r.getBloodGroup().display(),
                r.getComponentType(),
                r.getUnitsRequired(), r.getUnitsIssued(),
                r.getUrgency(), r.getStatus(),
                r.getRequiredBy(), r.getNotes(), r.getCreatedAt()
        );
    }
}
