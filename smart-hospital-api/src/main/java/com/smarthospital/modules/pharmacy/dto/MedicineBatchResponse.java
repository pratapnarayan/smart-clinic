package com.smarthospital.modules.pharmacy.dto;

import com.smarthospital.modules.pharmacy.domain.MedicineBatch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MedicineBatchResponse(
        UUID       id,
        UUID       medicineId,
        String     medicineName,
        String     batchNumber,
        LocalDate  expiryDate,
        int        quantity,
        BigDecimal purchasePrice,
        BigDecimal salePrice,
        boolean    expired,
        boolean    lowStock
) {
    public static MedicineBatchResponse from(MedicineBatch b) {
        return new MedicineBatchResponse(
                b.getId(),
                b.getMedicine().getId(),
                b.getMedicine().getName(),
                b.getBatchNumber(),
                b.getExpiryDate(),
                b.getQuantity(),
                b.getPurchasePrice(),
                b.getSalePrice(),
                b.isExpired(),
                b.isLowStock(b.getMedicine().getReorderLevel())
        );
    }
}
