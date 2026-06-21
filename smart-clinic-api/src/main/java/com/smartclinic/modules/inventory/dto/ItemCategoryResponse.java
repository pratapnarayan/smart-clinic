package com.smartclinic.modules.inventory.dto;

import com.smartclinic.modules.inventory.domain.ItemCategory;

import java.util.UUID;

public record ItemCategoryResponse(UUID id, String name, String description, boolean active) {
    public static ItemCategoryResponse from(ItemCategory c) {
        return new ItemCategoryResponse(c.getId(), c.getName(), c.getDescription(), c.isActive());
    }
}
