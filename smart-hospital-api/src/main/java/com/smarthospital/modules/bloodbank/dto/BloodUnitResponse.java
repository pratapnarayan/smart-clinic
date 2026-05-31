package com.smarthospital.modules.bloodbank.dto;

import com.smarthospital.modules.bloodbank.domain.BloodGroup;
import com.smarthospital.modules.bloodbank.domain.BloodUnit;
import com.smarthospital.modules.bloodbank.domain.BloodUnit.TestingStatus;
import com.smarthospital.modules.bloodbank.domain.BloodUnit.UnitStatus;
import com.smarthospital.modules.bloodbank.domain.ComponentType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BloodUnitResponse(
        UUID          id,
        String        unitNumber,
        BloodGroup    bloodGroup,
        String        bloodGroupDisplay,
        UUID          donorId,
        String        donorName,
        ComponentType componentType,
        int           volumeMl,
        LocalDate     collectionDate,
        LocalDate     expiryDate,
        TestingStatus testingStatus,
        UnitStatus    status,
        boolean       expired,
        String        notes,
        Instant       createdAt
) {
    public static BloodUnitResponse from(BloodUnit u) {
        return new BloodUnitResponse(
                u.getId(), u.getUnitNumber(),
                u.getBloodGroup(), u.getBloodGroup().display(),
                u.getDonorId(), u.getDonorName(),
                u.getComponentType(), u.getVolumeMl(),
                u.getCollectionDate(), u.getExpiryDate(),
                u.getTestingStatus(), u.getStatus(), u.isExpired(),
                u.getNotes(), u.getCreatedAt()
        );
    }
}
