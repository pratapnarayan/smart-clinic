package com.smartclinic.modules.ipd.dto;

public record IpdDashboardResponse(
        long totalAdmitted,
        long totalDischarged,
        long totalBeds,
        long availableBeds,
        long occupiedBeds
) {}
