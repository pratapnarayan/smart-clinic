package com.smartclinic.modules.clinic.dto;

import com.smartclinic.modules.clinic.domain.CollectionStatus;
import jakarta.validation.constraints.NotNull;

public record HomeCollectionStatusRequest(
        @NotNull CollectionStatus status,
        String failureReason
) {}
