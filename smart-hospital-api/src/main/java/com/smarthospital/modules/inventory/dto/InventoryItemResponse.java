package com.smarthospital.modules.inventory.dto;

import com.smarthospital.modules.inventory.domain.InventoryItem;

import java.time.Instant;
import java.util.UUID;

public record InventoryItemResponse(
        UUID    id,
        String  itemCode,
        String  name,
        String  description,
        UUID    categoryId,
        String  categoryName,
        String  unit,
        int     reorderLevel,
        int     currentStock,
        boolean lowStock,
        Instant createdAt
) {
    public static InventoryItemResponse from(InventoryItem i) {
        return new InventoryItemResponse(
                i.getId(), i.getItemCode(), i.getName(), i.getDescription(),
                i.getCategoryId(), i.getCategoryName(), i.getUnit(),
                i.getReorderLevel(), i.getCurrentStock(), i.isLowStock(),
                i.getCreatedAt()
        );
    }
}
