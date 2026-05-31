package com.smarthospital.modules.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeriodSummaryResponse(
        LocalDate  from,
        LocalDate  to,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netRevenue
) {}
