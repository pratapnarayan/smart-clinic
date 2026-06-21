package com.smartclinic.modules.radiology.dto;

public record RadiologyDashboardResponse(
        long pendingOrders,
        long scheduledOrders,
        long inProgressOrders,
        long completedOrders,
        long totalStudies
) {}
