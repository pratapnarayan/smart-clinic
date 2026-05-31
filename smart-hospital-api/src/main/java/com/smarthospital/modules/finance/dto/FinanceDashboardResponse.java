package com.smarthospital.modules.finance.dto;

import java.math.BigDecimal;
import java.util.List;

public record FinanceDashboardResponse(
        BigDecimal todayIncome,
        BigDecimal todayExpenses,
        BigDecimal todayNet,
        BigDecimal monthIncome,
        BigDecimal monthExpenses,
        BigDecimal monthNet,
        List<SourceBreakdown>   monthIncomeBySource,
        List<CategoryBreakdown> monthExpenseByCategory
) {
    public record SourceBreakdown(String source, BigDecimal amount) {}
    public record CategoryBreakdown(String category, BigDecimal amount) {}
}
