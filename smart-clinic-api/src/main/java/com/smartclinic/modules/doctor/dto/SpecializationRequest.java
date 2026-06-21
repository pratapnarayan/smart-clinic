package com.smartclinic.modules.doctor.dto;

import jakarta.validation.constraints.NotBlank;

public record SpecializationRequest(
    @NotBlank String name,
    @NotBlank String code,
    String description
) {}
