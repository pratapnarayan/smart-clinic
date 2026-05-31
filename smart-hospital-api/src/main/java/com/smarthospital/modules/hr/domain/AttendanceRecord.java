package com.smarthospital.modules.hr.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = @UniqueConstraint(name = "uq_attendance_emp_date",
                                          columnNames = {"employee_id", "attendance_date"}),
    indexes = {
        @Index(name = "idx_att_date",        columnList = "attendance_date"),
        @Index(name = "idx_att_employee_id", columnList = "employee_id")
    }
)
public class AttendanceRecord extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(length = 300)
    private String notes;

    public enum AttendanceStatus { PRESENT, ABSENT, HALF_DAY, ON_LEAVE, HOLIDAY }

    protected AttendanceRecord() {}

    public UUID             getId()             { return id; }
    public UUID             getEmployeeId()     { return employeeId; }
    public LocalDate        getAttendanceDate() { return attendanceDate; }
    public LocalTime        getCheckIn()        { return checkIn; }
    public LocalTime        getCheckOut()       { return checkOut; }
    public AttendanceStatus getStatus()         { return status; }
    public String           getNotes()          { return notes; }

    public void setCheckIn(LocalTime v)          { this.checkIn  = v; }
    public void setCheckOut(LocalTime v)         { this.checkOut = v; }
    public void setStatus(AttendanceStatus v)    { this.status   = v; }
    public void setNotes(String v)               { this.notes    = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final AttendanceRecord r = new AttendanceRecord();
        public Builder employeeId(UUID v)          { r.employeeId     = v; return this; }
        public Builder attendanceDate(LocalDate v)  { r.attendanceDate = v; return this; }
        public Builder checkIn(LocalTime v)         { r.checkIn        = v; return this; }
        public Builder checkOut(LocalTime v)        { r.checkOut       = v; return this; }
        public Builder status(AttendanceStatus v)   { r.status         = v; return this; }
        public Builder notes(String v)              { r.notes          = v; return this; }
        public AttendanceRecord build()             { return r; }
    }
}
