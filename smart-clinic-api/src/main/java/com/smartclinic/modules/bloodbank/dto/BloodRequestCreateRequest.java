package com.smartclinic.modules.bloodbank.dto;

import com.smartclinic.modules.bloodbank.domain.BloodGroup;
import com.smartclinic.modules.bloodbank.domain.BloodRequest.Urgency;
import com.smartclinic.modules.bloodbank.domain.ComponentType;
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
