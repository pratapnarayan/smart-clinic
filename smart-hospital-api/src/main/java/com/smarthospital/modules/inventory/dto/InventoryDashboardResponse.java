package com.smarthospital.modules.inventory.dto;

import java.math.BigDecimal;
import java.util.List;

public record InventoryDashboardResponse(
        long                       totalItems,
        long                       lowStockCount,
        long                       todayReceipts,
        long                       todayIssues,
        BigDecimal                 todayReceiptValue,
        List<InventoryItemResponse> lowStockItems
) {}
