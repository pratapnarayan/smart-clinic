package com.smartclinic.modules.pharmacy.dto;

import com.smartclinic.modules.pharmacy.domain.PharmacyBillItem;

import java.math.BigDecimal;
import java.util.UUID;

public record BillItemResponse(
        UUID       id,
        UUID       batchId,       // may be null if batch was later deleted
        String     medicineName,
        int        quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public static BillItemResponse from(PharmacyBillItem i) {
        return new BillItemResponse(
                i.getId(),
                i.getBatch() != null ? i.getBatch().getId() : null,
                i.getMedicineName(),
                i.getQuantity(),
                i.getUnitPrice(),
                i.getTotalPrice()
        );
    }
}
