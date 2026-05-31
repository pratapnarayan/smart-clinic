package com.smarthospital.modules.inventory.dto;

import com.smarthospital.modules.inventory.domain.ItemCategory;

import java.util.UUID;

public record ItemCategoryResponse(UUID id, String name, String description, boolean active) {
    public static ItemCategoryResponse from(ItemCategory c) {
        return new ItemCategoryResponse(c.getId(), c.getName(), c.getDescription(), c.isActive());
    }
}
