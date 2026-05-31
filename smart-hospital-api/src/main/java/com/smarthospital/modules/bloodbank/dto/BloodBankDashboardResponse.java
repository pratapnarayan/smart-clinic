package com.smarthospital.modules.bloodbank.dto;

import java.util.List;

public record BloodBankDashboardResponse(
        long                    totalDonors,
        long                    activeDonors,
        long                    totalAvailable,
        long                    pendingTesting,
        long                    openRequests,
        long                    todayIssues,
        List<BloodGroupStock>   stockByGroup
) {
    public record BloodGroupStock(
            String bloodGroup,
            String display,
            long   available,
            long   pendingTesting
    ) {}
}
