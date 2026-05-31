package com.smarthospital.modules.hr.dto;

import com.smarthospital.modules.hr.domain.HrDepartment;

import java.util.UUID;

public record DepartmentResponse(UUID id, String name, String code, boolean active) {
    public static DepartmentResponse from(HrDepartment d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getCode(), d.isActive());
    }
}
