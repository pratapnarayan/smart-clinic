package com.smartclinic.modules.finance.dto;

import com.smartclinic.modules.finance.domain.ExpenseEntry.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseEntryRequest(
        LocalDate   entryDate,
        @NotNull    UUID        categoryId,
        @NotBlank   String      description,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull    PaymentMode paymentMode,
        String      referenceNo,
        String      paidTo,
        String      approvedBy,
        String      notes
) {}
