package com.smartclinic.modules.hr.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(@NotBlank String name, @NotBlank String code) {}
