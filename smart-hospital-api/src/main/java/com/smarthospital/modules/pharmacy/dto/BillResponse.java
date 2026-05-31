package com.smarthospital.modules.pharmacy.dto;

import com.smarthospital.modules.pharmacy.domain.PharmacyBill;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BillResponse(
        UUID                 id,
        String               billNumber,
        UUID                 patientId,
        String               patientName,
        BigDecimal           totalAmount,
        BigDecimal           discount,
        BigDecimal           netAmount,
        String               paymentMode,
        String               status,
        List<BillItemResponse> items,
        Instant              createdAt
) {
    public static BillResponse from(PharmacyBill b) {
        return new BillResponse(
                b.getId(),
                b.getBillNumber(),
                b.getPatientId(),
                b.getPatientName(),
                b.getTotalAmount(),
                b.getDiscount(),
                b.getNetAmount(),
                b.getPaymentMode(),
                b.getStatus(),
                b.getItems().stream().map(BillItemResponse::from).toList(),
                b.getCreatedAt()
        );
    }
}
