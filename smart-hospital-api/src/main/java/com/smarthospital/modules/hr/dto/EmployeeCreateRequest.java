package com.smarthospital.modules.hr.dto;

import com.smarthospital.modules.hr.domain.Employee.EmploymentType;
import com.smarthospital.modules.hr.domain.Employee.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeCreateRequest(
        @NotBlank String         firstName,
        @NotBlank String         lastName,
        @NotNull  LocalDate      joinDate,
        LocalDate                dateOfBirth,
        Gender                   gender,
        String                   mobile,
        String                   email,
        String                   address,
        String                   bloodGroup,
        UUID                     departmentId,
        UUID                     designationId,
        UUID                     userId,
        EmploymentType           employmentType
) {}
