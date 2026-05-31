package com.smarthospital.modules.ipd.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IpdAdmissionCreateRequest(
        @NotNull UUID   patientId,
        UUID            opdVisitId,
        @NotNull UUID   wardId,
        @NotNull UUID   bedId,
        UUID            doctorId,
        String          doctorName,
        String          admissionDiagnosis,
        String          notes
) {}
