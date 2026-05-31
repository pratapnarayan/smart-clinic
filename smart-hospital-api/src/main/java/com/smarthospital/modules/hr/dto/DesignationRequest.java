package com.smarthospital.modules.hr.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DesignationRequest(@NotBlank String title, UUID departmentId) {}
