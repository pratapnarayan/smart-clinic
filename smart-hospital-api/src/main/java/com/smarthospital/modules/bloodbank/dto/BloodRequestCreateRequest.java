package com.smarthospital.modules.bloodbank.dto;

import com.smarthospital.modules.bloodbank.domain.BloodGroup;
import com.smarthospital.modules.bloodbank.domain.BloodRequest.Urgency;
import com.smarthospital.modules.bloodbank.domain.ComponentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record BloodRequestCreateRequest(
        UUID          patientId,
        @NotBlank String       patientName,
        String        requestedBy,
        @NotNull BloodGroup    bloodGroup,
        @NotNull ComponentType componentType,
        @Min(1)  int           unitsRequired,
        Urgency       urgency,
        LocalDateTime requiredBy,
        String        notes
) {}
