package com.smarthospital.modules.frontoffice.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "opd_tokens",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_token_date_dept_number",
        columnNames = {"token_date", "department", "token_number"}
    ),
    indexes = {
        @Index(name = "idx_token_date_dept",  columnList = "token_date, department"),
        @Index(name = "idx_token_patient_id", columnList = "patient_id")
    }
)
public class OpdToken extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token_number", nullable = false, length = 20)
    private String tokenNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "patient_mobile", length = 15)
    private String patientMobile;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    @Column(name = "token_date", nullable = false)
    private LocalDate tokenDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenPriority priority = TokenPriority.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenStatus status = TokenStatus.WAITING;

    @Column(name = "linked_appointment_id")
    private UUID linkedAppointmentId;

    public enum TokenPriority { NORMAL, URGENT }
    public enum TokenStatus   { WAITING, IN_PROGRESS, COMPLETED, SKIPPED }

    protected OpdToken() {}

    public UUID          getId()                   { return id; }
    public String        getTokenNumber()          { return tokenNumber; }
    public UUID          getPatientId()            { return patientId; }
    public String        getPatientName()          { return patientName; }
    public String        getPatientMobile()        { return patientMobile; }
    public String        getDepartment()           { return department; }
    public UUID          getDoctorId()             { return doctorId; }
    public String        getDoctorName()           { return doctorName; }
    public LocalDate     getTokenDate()            { return tokenDate; }
    public TokenPriority getPriority()             { return priority; }
    public TokenStatus   getStatus()               { return status; }
    public UUID          getLinkedAppointmentId()  { return linkedAppointmentId; }

    public void setStatus(TokenStatus v)           { this.status = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final OpdToken t = new OpdToken();
        public Builder tokenNumber(String v)           { t.tokenNumber          = v; return this; }
        public Builder patientId(UUID v)               { t.patientId            = v; return this; }
        public Builder patientName(String v)           { t.patientName          = v; return this; }
        public Builder patientMobile(String v)         { t.patientMobile        = v; return this; }
        public Builder department(String v)            { t.department           = v; return this; }
        public Builder doctorId(UUID v)                { t.doctorId             = v; return this; }
        public Builder doctorName(String v)            { t.doctorName           = v; return this; }
        public Builder tokenDate(LocalDate v)          { t.tokenDate            = v; return this; }
        public Builder priority(TokenPriority v)       { t.priority             = v; return this; }
        public Builder linkedAppointmentId(UUID v)     { t.linkedAppointmentId  = v; return this; }
        public OpdToken build()                        { return t; }
    }
}
