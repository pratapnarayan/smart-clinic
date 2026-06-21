package com.smartclinic.modules.operation.dto;

import java.util.List;

public record OtDashboardResponse(
        long                      todayScheduled,
        long                      todayInProgress,
        long                      todayCompleted,
        long                      monthTotal,
        List<OtScheduleResponse>  todaySchedules,
        List<TheatreUtilization>  theatreUtilization
) {
    public record TheatreUtilization(String theatreName, long operationsThisMonth) {}
}
