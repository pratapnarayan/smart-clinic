package com.smarthospital.modules.pharmacy.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Per-medicine stock summary shown on the pharmacy stock dashboard. */
public record StockSummaryResponse(
        UUID       medicineId,
        String     medicineName,
        String     genericName,
        String     unit,
        int        totalStock,
        int        reorderLevel,
        boolean    lowStock,
        BigDecimal lowestSalePrice,
        LocalDate  nearestExpiry,
        List<MedicineBatchResponse> batches
) {}
