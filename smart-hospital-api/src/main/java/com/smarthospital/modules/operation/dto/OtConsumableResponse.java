package com.smarthospital.modules.operation.dto;

import com.smarthospital.modules.operation.domain.OtConsumable;

import java.util.UUID;

public record OtConsumableResponse(UUID id, UUID itemId, String itemName, String itemUnit, int quantityUsed) {
    public static OtConsumableResponse from(OtConsumable c) {
        return new OtConsumableResponse(c.getId(), c.getItemId(), c.getItemName(), c.getItemUnit(), c.getQuantityUsed());
    }
}
