package com.smarthospital.modules.finance.dto;

import jakarta.validation.constraints.NotBlank;

public record ExpenseCategoryRequest(@NotBlank String name, String description) {}
