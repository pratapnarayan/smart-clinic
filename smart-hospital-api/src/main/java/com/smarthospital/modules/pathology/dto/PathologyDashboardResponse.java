package com.smarthospital.modules.pathology.dto;

public record PathologyDashboardResponse(
        long pendingOrders,
        long sampleCollected,
        long inProgressOrders,
        long completedToday,
        long totalTests
) {}
