package com.smarthospital.modules.frontoffice.dto;

import com.smarthospital.modules.frontoffice.domain.OpdToken.TokenPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OpdTokenCreateRequest(
        @NotNull  UUID          patientId,
        @NotBlank String        department,
        UUID                    doctorId,
        String                  doctorName,
        TokenPriority           priority,
        UUID                    linkedAppointmentId
) {}
