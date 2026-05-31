package com.smarthospital.modules.bloodbank.dto;

import com.smarthospital.modules.bloodbank.domain.BloodGroup;
import com.smarthospital.modules.bloodbank.domain.ComponentType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record BloodUnitCreateRequest(
        @NotNull BloodGroup     bloodGroup,
        UUID                    donorId,
        String                  donorName,
        @NotNull ComponentType  componentType,
        Integer                 volumeMl,
        LocalDate               collectionDate,
        LocalDate               expiryDate,
        String                  notes
) {}
