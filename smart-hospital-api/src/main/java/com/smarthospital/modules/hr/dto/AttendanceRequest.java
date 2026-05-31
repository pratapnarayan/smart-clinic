package com.smarthospital.modules.hr.dto;

import com.smarthospital.modules.hr.domain.AttendanceRecord.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AttendanceRequest(
        @NotNull UUID             employeeId,
        @NotNull LocalDate        attendanceDate,
        LocalTime                 checkIn,
        LocalTime                 checkOut,
        @NotNull AttendanceStatus status,
        String                    notes
) {}
