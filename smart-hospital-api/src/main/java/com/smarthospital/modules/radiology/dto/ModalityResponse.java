package com.smarthospital.modules.radiology.dto;

import com.smarthospital.modules.radiology.domain.ImagingModality;

import java.util.UUID;

public record ModalityResponse(UUID id, String name, String code, String description, boolean active) {
    public static ModalityResponse from(ImagingModality m) {
        return new ModalityResponse(m.getId(), m.getName(), m.getCode(), m.getDescription(), m.isActive());
    }
}
