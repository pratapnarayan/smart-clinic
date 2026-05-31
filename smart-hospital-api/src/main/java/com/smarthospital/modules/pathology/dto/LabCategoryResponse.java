package com.smarthospital.modules.pathology.dto;

import com.smarthospital.modules.pathology.domain.LabTestCategory;

import java.util.UUID;

public record LabCategoryResponse(UUID id, String name, String description, boolean active) {
    public static LabCategoryResponse from(LabTestCategory c) {
        return new LabCategoryResponse(c.getId(), c.getName(), c.getDescription(), c.isActive());
    }
}
