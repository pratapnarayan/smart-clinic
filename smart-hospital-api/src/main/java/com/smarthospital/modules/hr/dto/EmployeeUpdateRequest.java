package com.smarthospital.modules.hr.dto;

import com.smarthospital.modules.hr.domain.Employee.EmployeeStatus;
import com.smarthospital.modules.hr.domain.Employee.EmploymentType;
import com.smarthospital.modules.hr.domain.Employee.Gender;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeUpdateRequest(
        String         firstName,
        String         lastName,
        LocalDate      dateOfBirth,
        Gender         gender,
        String         mobile,
        String         email,
        String         address,
        String         bloodGroup,
        UUID           departmentId,
        UUID           designationId,
        UUID           userId,
        EmploymentType employmentType,
        EmployeeStatus status
) {}
