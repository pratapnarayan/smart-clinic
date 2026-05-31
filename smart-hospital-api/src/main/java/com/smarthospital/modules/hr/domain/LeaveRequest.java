package com.smarthospital.modules.hr.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "leave_requests",
    indexes = {
        @Index(name = "idx_leave_employee_id", columnList = "employee_id"),
        @Index(name = "idx_leave_status",      columnList = "status"),
        @Index(name = "idx_leave_dates",       columnList = "from_date, to_date")
    }
)
public class LeaveRequest extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "leave_number", nullable = false, unique = true, length = 30)
    private String leaveNumber;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "employee_name", nullable = false, length = 200)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 20)
    private LeaveType leaveType;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approver_notes", columnDefinition = "TEXT")
    private String approverNotes;

    public enum LeaveType   { CASUAL, SICK, EARNED, MATERNITY, PATERNITY, UNPAID }
    public enum LeaveStatus { PENDING, APPROVED, REJECTED, CANCELLED }

    protected LeaveRequest() {}

    public UUID        getId()           { return id; }
    public String      getLeaveNumber()  { return leaveNumber; }
    public UUID        getEmployeeId()   { return employeeId; }
    public String      getEmployeeName() { return employeeName; }
    public LeaveType   getLeaveType()    { return leaveType; }
    public LocalDate   getFromDate()     { return fromDate; }
    public LocalDate   getToDate()       { return toDate; }
    public int         getTotalDays()    { return totalDays; }
    public String      getReason()       { return reason; }
    public LeaveStatus getStatus()       { return status; }
    public UUID        getApprovedById() { return approvedById; }
    public String      getApproverNotes(){ return approverNotes; }

    public void setStatus(LeaveStatus v)       { this.status        = v; }
    public void setApprovedById(UUID v)        { this.approvedById  = v; }
    public void setApproverNotes(String v)     { this.approverNotes = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final LeaveRequest l = new LeaveRequest();
        public Builder leaveNumber(String v)   { l.leaveNumber  = v; return this; }
        public Builder employeeId(UUID v)      { l.employeeId   = v; return this; }
        public Builder employeeName(String v)  { l.employeeName = v; return this; }
        public Builder leaveType(LeaveType v)  { l.leaveType    = v; return this; }
        public Builder fromDate(LocalDate v)   { l.fromDate     = v; return this; }
        public Builder toDate(LocalDate v)     { l.toDate       = v; return this; }
        public Builder totalDays(int v)        { l.totalDays    = v; return this; }
        public Builder reason(String v)        { l.reason       = v; return this; }
        public LeaveRequest build()            { return l; }
    }
}
