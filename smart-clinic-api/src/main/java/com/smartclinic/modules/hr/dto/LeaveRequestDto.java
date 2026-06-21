package com.smartclinic.modules.hr.dto;

import com.smartclinic.modules.hr.domain.LeaveRequest;
import com.smartclinic.modules.hr.domain.LeaveRequest.LeaveStatus;
import com.smartclinic.modules.hr.domain.LeaveRequest.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class LeaveRequestDto {

    public record CreateRequest(
            @NotNull UUID      employeeId,
            @NotNull LeaveType leaveType,
            @NotNull LocalDate fromDate,
            @NotNull LocalDate toDate,
            String             reason
    ) {}

    public record ApproveRequest(String approverNotes) {}

    public record Response(
            UUID        id,
            String      leaveNumber,
            UUID        employeeId,
            String      employeeName,
            LeaveType   leaveType,
            LocalDate   fromDate,
            LocalDate   toDate,
            int         totalDays,
            String      reason,
            LeaveStatus status,
            UUID        approvedById,
            String      approverNotes,
            Instant     createdAt
    ) {
        public static Response from(LeaveRequest l) {
            return new Response(
                    l.getId(), l.getLeaveNumber(),
                    l.getEmployeeId(), l.getEmployeeName(),
                    l.getLeaveType(), l.getFromDate(), l.getToDate(), l.getTotalDays(),
                    l.getReason(), l.getStatus(),
                    l.getApprovedById(), l.getApproverNotes(),
                    l.getCreatedAt()
            );
        }
    }
}
