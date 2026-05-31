package com.smarthospital.modules.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InventoryItemRequest(
        @NotBlank String itemCode,
        @NotBlank String name,
        String           description,
        @NotNull  UUID   categoryId,
        @NotBlank String unit,
        @Min(0)   int    reorderLevel
) {}
