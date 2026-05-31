package com.smarthospital.modules.hr.dto;

public record HrDashboardResponse(
        long totalEmployees,
        long activeEmployees,
        long presentToday,
        long absentToday,
        long onLeaveToday,
        long pendingLeaveRequests
) {}
