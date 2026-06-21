package com.smartclinic.modules.finance.dto;

import com.smartclinic.modules.finance.domain.IncomeEntry;
import com.smartclinic.modules.finance.domain.IncomeEntry.PaymentMode;
import com.smartclinic.modules.finance.domain.IncomeEntry.SourceType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record IncomeEntryResponse(
        UUID        id,
        String      entryNumber,
        LocalDate   entryDate,
        SourceType  sourceType,
        UUID        sourceId,
        String      patientName,
        BigDecimal  amount,
        String      description,
        PaymentMode paymentMode,
        String      referenceNo,
        String      receivedBy,
        String      notes,
        Instant     createdAt
) {
    public static IncomeEntryResponse from(IncomeEntry e) {
        return new IncomeEntryResponse(
                e.getId(), e.getEntryNumber(), e.getEntryDate(),
                e.getSourceType(), e.getSourceId(), e.getPatientName(),
                e.getAmount(), e.getDescription(), e.getPaymentMode(),
                e.getReferenceNo(), e.getReceivedBy(), e.getNotes(),
                e.getCreatedAt()
        );
    }
}
