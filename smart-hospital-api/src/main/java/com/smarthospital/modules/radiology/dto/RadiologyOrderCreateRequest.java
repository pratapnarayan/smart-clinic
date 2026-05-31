package com.smarthospital.modules.radiology.dto;

import com.smarthospital.modules.radiology.domain.RadiologyOrder.Priority;
import com.smarthospital.modules.radiology.domain.RadiologyOrder.SourceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RadiologyOrderCreateRequest(
        @NotNull  UUID            patientId,
        UUID                      referredById,
        String                    referredByName,
        SourceType                sourceType,
        UUID                      sourceId,
        Priority                  priority,
        LocalDateTime             scheduledAt,
        String                    clinicalHistory,
        String                    notes,
        @NotEmpty List<UUID>      studyIds
) {}
