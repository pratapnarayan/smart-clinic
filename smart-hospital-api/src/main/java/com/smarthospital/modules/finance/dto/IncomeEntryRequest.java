package com.smarthospital.modules.finance.dto;

import com.smarthospital.modules.finance.domain.IncomeEntry.PaymentMode;
import com.smarthospital.modules.finance.domain.IncomeEntry.SourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IncomeEntryRequest(
        LocalDate   entryDate,
        @NotNull    SourceType  sourceType,
        UUID        sourceId,
        String      patientName,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank   String      description,
        @NotNull    PaymentMode paymentMode,
        String      referenceNo,
        String      receivedBy,
        String      notes
) {}
