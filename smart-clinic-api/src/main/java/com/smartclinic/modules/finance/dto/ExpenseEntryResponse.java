package com.smartclinic.modules.finance.dto;

import com.smartclinic.modules.finance.domain.ExpenseEntry;
import com.smartclinic.modules.finance.domain.ExpenseEntry.PaymentMode;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseEntryResponse(
        UUID        id,
        String      entryNumber,
        LocalDate   entryDate,
        UUID        categoryId,
        String      categoryName,
        String      description,
        BigDecimal  amount,
        PaymentMode paymentMode,
        String      referenceNo,
        String      paidTo,
        String      approvedBy,
        String      notes,
        Instant     createdAt
) {
    public static ExpenseEntryResponse from(ExpenseEntry e) {
        return new ExpenseEntryResponse(
                e.getId(), e.getEntryNumber(), e.getEntryDate(),
                e.getCategoryId(), e.getCategoryName(),
                e.getDescription(), e.getAmount(), e.getPaymentMode(),
                e.getReferenceNo(), e.getPaidTo(), e.getApprovedBy(),
                e.getNotes(), e.getCreatedAt()
        );
    }
}
