package com.smarthospital.modules.inventory.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemCategoryRequest(@NotBlank String name, String description) {}
