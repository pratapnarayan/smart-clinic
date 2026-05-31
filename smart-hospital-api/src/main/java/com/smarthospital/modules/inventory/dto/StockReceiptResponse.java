package com.smarthospital.modules.inventory.dto;

import com.smarthospital.modules.inventory.domain.StockReceipt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StockReceiptResponse(
        UUID       id,
        String     receiptNumber,
        LocalDate  entryDate,
        UUID       itemId,
        String     itemName,
        String     itemUnit,
        int        quantity,
        BigDecimal unitCost,
        BigDecimal totalCost,
        String     supplierName,
        String     grnNumber,
        String     receivedBy,
        String     notes,
        Instant    createdAt
) {
    public static StockReceiptResponse from(StockReceipt r) {
        return new StockReceiptResponse(
                r.getId(), r.getReceiptNumber(), r.getEntryDate(),
                r.getItemId(), r.getItemName(), r.getItemUnit(),
                r.getQuantity(), r.getUnitCost(), r.getTotalCost(),
                r.getSupplierName(), r.getGrnNumber(), r.getReceivedBy(),
                r.getNotes(), r.getCreatedAt()
        );
    }
}
