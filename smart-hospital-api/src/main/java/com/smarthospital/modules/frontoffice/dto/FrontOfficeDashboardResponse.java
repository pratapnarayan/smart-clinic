package com.smarthospital.modules.frontoffice.dto;

public record FrontOfficeDashboardResponse(
        long todayAppointments,
        long confirmedAppointments,
        long checkedInAppointments,
        long todayTokens,
        long waitingTokens,
        long inProgressTokens,
        long completedTokens
) {}
