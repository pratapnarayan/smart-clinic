package com.smartclinic.modules.pathology.dto;

import com.smartclinic.modules.pathology.domain.LabOrder.Priority;
import com.smartclinic.modules.pathology.domain.LabOrder.SourceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record LabOrderCreateRequest(
        @NotNull  UUID         patientId,
        UUID                   referredById,
        String                 referredByName,
        SourceType             sourceType,
        UUID                   sourceId,
        Priority               priority,
        String                 notes,
        @NotEmpty List<UUID>   testIds
) {}
