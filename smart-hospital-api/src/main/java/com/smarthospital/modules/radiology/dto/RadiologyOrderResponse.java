package com.smarthospital.modules.radiology.dto;

import com.smarthospital.modules.radiology.domain.RadiologyOrder;
import com.smarthospital.modules.radiology.domain.RadiologyOrder.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RadiologyOrderResponse(
        UUID                           id,
        String                         orderNumber,
        UUID                           patientId,
        String                         patientName,
        String                         patientMobile,
        UUID                           referredById,
        String                         referredByName,
        SourceType                     sourceType,
        UUID                           sourceId,
        Priority                       priority,
        OrderStatus                    status,
        LocalDateTime                  scheduledAt,
        String                         clinicalHistory,
        BigDecimal                     totalAmount,
        BigDecimal                     discount,
        BigDecimal                     netAmount,
        PaymentStatus                  paymentStatus,
        String                         notes,
        List<RadiologyOrderItemResponse> items,
        Instant                        createdAt
) {
    public static RadiologyOrderResponse from(RadiologyOrder o) {
        return new RadiologyOrderResponse(
                o.getId(), o.getOrderNumber(),
                o.getPatientId(), o.getPatientName(), o.getPatientMobile(),
                o.getReferredById(), o.getReferredByName(),
                o.getSourceType(), o.getSourceId(),
                o.getPriority(), o.getStatus(), o.getScheduledAt(),
                o.getClinicalHistory(),
                o.getTotalAmount(), o.getDiscount(), o.getNetAmount(), o.getPaymentStatus(),
                o.getNotes(),
                o.getItems().stream().map(RadiologyOrderItemResponse::from).toList(),
                o.getCreatedAt()
        );
    }
}
