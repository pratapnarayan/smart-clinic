package com.smartclinic.modules.pathology.dto;

import com.smartclinic.modules.pathology.domain.LabOrder;
import com.smartclinic.modules.pathology.domain.LabOrder.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LabOrderResponse(
        UUID                      id,
        String                    orderNumber,
        UUID                      patientId,
        String                    patientName,
        String                    patientMobile,
        UUID                      referredById,
        String                    referredByName,
        SourceType                sourceType,
        UUID                      sourceId,
        Priority                  priority,
        OrderStatus               status,
        LocalDateTime             sampleCollectedAt,
        BigDecimal                totalAmount,
        BigDecimal                discount,
        BigDecimal                netAmount,
        PaymentStatus             paymentStatus,
        String                    notes,
        List<LabOrderItemResponse> items,
        Instant                   createdAt
) {
    public static LabOrderResponse from(LabOrder o) {
        return new LabOrderResponse(
                o.getId(), o.getOrderNumber(),
                o.getPatientId(), o.getPatientName(), o.getPatientMobile(),
                o.getReferredById(), o.getReferredByName(),
                o.getSourceType(), o.getSourceId(),
                o.getPriority(), o.getStatus(), o.getSampleCollectedAt(),
                o.getTotalAmount(), o.getDiscount(), o.getNetAmount(), o.getPaymentStatus(),
                o.getNotes(),
                o.getItems().stream().map(LabOrderItemResponse::from).toList(),
                o.getCreatedAt()
        );
    }
}
