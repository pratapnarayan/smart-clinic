package com.smarthospital.modules.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicineCategoryRequest(
        @NotBlank @Size(max = 100) String name
) {}
