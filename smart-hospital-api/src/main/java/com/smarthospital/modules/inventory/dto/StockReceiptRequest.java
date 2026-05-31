package com.smarthospital.modules.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record StockReceiptRequest(
        LocalDate  entryDate,
        @NotNull   UUID       itemId,
        @Min(1)    int        quantity,
        BigDecimal unitCost,
        String     supplierName,
        String     grnNumber,
        String     receivedBy,
        String     notes
) {}
