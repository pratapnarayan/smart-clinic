package com.smarthospital.modules.operation.dto;

import com.smarthospital.modules.operation.domain.OperationTheatre.TheatreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TheatreRequest(
        @NotBlank String      theatreNumber,
        @NotBlank String      name,
        @NotNull  TheatreType type
) {}
