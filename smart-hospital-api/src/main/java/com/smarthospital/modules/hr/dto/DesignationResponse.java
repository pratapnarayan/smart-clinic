package com.smarthospital.modules.hr.dto;

import com.smarthospital.modules.hr.domain.Designation;

import java.util.UUID;

public record DesignationResponse(UUID id, String title, UUID departmentId, boolean active) {
    public static DesignationResponse from(Designation d) {
        return new DesignationResponse(d.getId(), d.getTitle(), d.getDepartmentId(), d.isActive());
    }
}
