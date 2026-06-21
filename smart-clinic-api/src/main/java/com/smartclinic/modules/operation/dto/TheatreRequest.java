package com.smartclinic.modules.operation.dto;

import com.smartclinic.modules.operation.domain.OperationTheatre.TheatreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TheatreRequest(
        @NotBlank String      theatreNumber,
        @NotBlank String      name,
        @NotNull  TheatreType type
) {}
