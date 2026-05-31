package com.smarthospital.modules.operation.dto;

import com.smarthospital.modules.operation.domain.OperationTheatre;
import com.smarthospital.modules.operation.domain.OperationTheatre.TheatreType;

import java.util.UUID;

public record TheatreResponse(UUID id, String theatreNumber, String name, TheatreType type, boolean active) {
    public static TheatreResponse from(OperationTheatre t) {
        return new TheatreResponse(t.getId(), t.getTheatreNumber(), t.getName(), t.getType(), t.isActive());
    }
}
