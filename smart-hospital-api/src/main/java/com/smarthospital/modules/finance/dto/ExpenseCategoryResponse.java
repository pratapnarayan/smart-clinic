package com.smarthospital.modules.finance.dto;

import com.smarthospital.modules.finance.domain.ExpenseCategory;

import java.util.UUID;

public record ExpenseCategoryResponse(UUID id, String name, String description, boolean active) {
    public static ExpenseCategoryResponse from(ExpenseCategory c) {
        return new ExpenseCategoryResponse(c.getId(), c.getName(), c.getDescription(), c.isActive());
    }
}
