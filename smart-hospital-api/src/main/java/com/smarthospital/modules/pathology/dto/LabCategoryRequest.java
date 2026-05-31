package com.smarthospital.modules.pathology.dto;

import jakarta.validation.constraints.NotBlank;

public record LabCategoryRequest(@NotBlank String name, String description) {}
