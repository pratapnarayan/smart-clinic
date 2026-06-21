package com.smartclinic.modules.hr.dto;

import com.smartclinic.modules.hr.domain.AttendanceRecord;
import com.smartclinic.modules.hr.domain.AttendanceRecord.AttendanceStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AttendanceResponse(
        UUID             id,
        UUID             employeeId,
        LocalDate        attendanceDate,
        LocalTime        checkIn,
        LocalTime        checkOut,
        AttendanceStatus status,
        String           notes,
        Instant          createdAt
) {
    public static AttendanceResponse from(AttendanceRecord r) {
        return new AttendanceResponse(
                r.getId(), r.getEmployeeId(), r.getAttendanceDate(),
                r.getCheckIn(), r.getCheckOut(), r.getStatus(),
                r.getNotes(), r.getCreatedAt()
        );
    }
}
