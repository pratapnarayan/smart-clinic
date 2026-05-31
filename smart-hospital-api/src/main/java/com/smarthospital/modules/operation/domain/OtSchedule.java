package com.smarthospital.modules.operation.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "ot_schedules",
    indexes = {
        @Index(name = "idx_ot_sched_date",    columnList = "scheduled_date"),
        @Index(name = "idx_ot_sched_theatre", columnList = "theatre_id, scheduled_date"),
        @Index(name = "idx_ot_sched_status",  columnList = "status"),
        @Index(name = "idx_ot_sched_patient", columnList = "patient_id"),
        @Index(name = "idx_ot_sched_surgeon", columnList = "surgeon_id")
    }
)
public class OtSchedule extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "schedule_number", nullable = false, unique = true, length = 30)
    private String scheduleNumber;

    @Column(name = "admission_id")
    private UUID admissionId;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "theatre_id", nullable = false)
    private UUID theatreId;

    @Column(name = "theatre_name", nullable = false, length = 100)
    private String theatreName;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_start", nullable = false)
    private LocalDateTime scheduledStart;

    @Column(name = "estimated_duration_mins", nullable = false)
    private int estimatedDurationMins = 60;

    @Column(name = "procedure_name", nullable = false, length = 300)
    private String procedureName;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private OperationType operationType = OperationType.ELECTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.ROUTINE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.SCHEDULED;

    @Column(name = "surgeon_id")
    private UUID surgeonId;

    @Column(name = "surgeon_name", length = 200)
    private String surgeonName;

    @Column(name = "anesthetist_id")
    private UUID anesthetistId;

    @Column(name = "anesthetist_name", length = 200)
    private String anesthetistName;

    @Column(name = "assistant_names", columnDefinition = "TEXT")
    private String assistantNames;

    @Column(name = "pre_op_diagnosis", columnDefinition = "TEXT")
    private String preOpDiagnosis;

    @Column(name = "blood_request_id")
    private UUID bloodRequestId;

    @Column(name = "blood_request_number", length = 30)
    private String bloodRequestNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Post-op fields (populated on completion) ──────────────────────────────

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "anesthesia_type", length = 20)
    private AnesthesiaType anesthesiaType;

    @Column(name = "post_op_diagnosis", columnDefinition = "TEXT")
    private String postOpDiagnosis;

    @Column(name = "procedure_details", columnDefinition = "TEXT")
    private String procedureDetails;

    @Column(columnDefinition = "TEXT")
    private String complications;

    @Column(name = "surgeon_notes", columnDefinition = "TEXT")
    private String surgeonNotes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Outcome outcome;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_condition_after", length = 20)
    private PatientCondition patientConditionAfter;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OtConsumable> consumables = new ArrayList<>();

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum OperationType   { ELECTIVE, EMERGENCY, DIAGNOSTIC }
    public enum Priority        { ROUTINE, URGENT, EMERGENCY }
    public enum Status          { SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED }
    public enum AnesthesiaType  { GENERAL, SPINAL, EPIDURAL, LOCAL, REGIONAL }
    public enum Outcome         { SUCCESSFUL, COMPLICATED, INCOMPLETE }
    public enum PatientCondition { STABLE, CRITICAL, DECEASED }

    protected OtSchedule() {}

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID             getId()                    { return id; }
    public String           getScheduleNumber()        { return scheduleNumber; }
    public UUID             getAdmissionId()           { return admissionId; }
    public UUID             getPatientId()             { return patientId; }
    public String           getPatientName()           { return patientName; }
    public UUID             getTheatreId()             { return theatreId; }
    public String           getTheatreName()           { return theatreName; }
    public LocalDate        getScheduledDate()         { return scheduledDate; }
    public LocalDateTime    getScheduledStart()        { return scheduledStart; }
    public int              getEstimatedDurationMins() { return estimatedDurationMins; }
    public String           getProcedureName()         { return procedureName; }
    public OperationType    getOperationType()         { return operationType; }
    public Priority         getPriority()              { return priority; }
    public Status           getStatus()                { return status; }
    public UUID             getSurgeonId()             { return surgeonId; }
    public String           getSurgeonName()           { return surgeonName; }
    public UUID             getAnesthetistId()         { return anesthetistId; }
    public String           getAnesthetistName()       { return anesthetistName; }
    public String           getAssistantNames()        { return assistantNames; }
    public String           getPreOpDiagnosis()        { return preOpDiagnosis; }
    public UUID             getBloodRequestId()        { return bloodRequestId; }
    public String           getBloodRequestNumber()    { return bloodRequestNumber; }
    public String           getNotes()                 { return notes; }
    public LocalDateTime    getActualStart()           { return actualStart; }
    public LocalDateTime    getActualEnd()             { return actualEnd; }
    public AnesthesiaType   getAnesthesiaType()        { return anesthesiaType; }
    public String           getPostOpDiagnosis()       { return postOpDiagnosis; }
    public String           getProcedureDetails()      { return procedureDetails; }
    public String           getComplications()         { return complications; }
    public String           getSurgeonNotes()          { return surgeonNotes; }
    public Outcome          getOutcome()               { return outcome; }
    public PatientCondition getPatientConditionAfter() { return patientConditionAfter; }
    public List<OtConsumable> getConsumables()         { return consumables; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setStatus(Status v)                     { this.status                = v; }
    public void setNotes(String v)                      { this.notes                 = v; }
    public void setActualStart(LocalDateTime v)         { this.actualStart           = v; }
    public void setActualEnd(LocalDateTime v)           { this.actualEnd             = v; }
    public void setAnesthesiaType(AnesthesiaType v)     { this.anesthesiaType        = v; }
    public void setPostOpDiagnosis(String v)            { this.postOpDiagnosis       = v; }
    public void setProcedureDetails(String v)           { this.procedureDetails      = v; }
    public void setComplications(String v)              { this.complications         = v; }
    public void setSurgeonNotes(String v)               { this.surgeonNotes          = v; }
    public void setOutcome(Outcome v)                   { this.outcome               = v; }
    public void setPatientConditionAfter(PatientCondition v){ this.patientConditionAfter = v; }

    // ── Builder ───────────────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final OtSchedule s = new OtSchedule();
        public Builder scheduleNumber(String v)         { s.scheduleNumber        = v; return this; }
        public Builder admissionId(UUID v)              { s.admissionId           = v; return this; }
        public Builder patientId(UUID v)                { s.patientId             = v; return this; }
        public Builder patientName(String v)            { s.patientName           = v; return this; }
        public Builder theatreId(UUID v)                { s.theatreId             = v; return this; }
        public Builder theatreName(String v)            { s.theatreName           = v; return this; }
        public Builder scheduledDate(LocalDate v)       { s.scheduledDate         = v; return this; }
        public Builder scheduledStart(LocalDateTime v)  { s.scheduledStart        = v; return this; }
        public Builder estimatedDurationMins(int v)     { s.estimatedDurationMins = v; return this; }
        public Builder procedureName(String v)          { s.procedureName         = v; return this; }
        public Builder operationType(OperationType v)   { s.operationType         = v; return this; }
        public Builder priority(Priority v)             { s.priority              = v; return this; }
        public Builder surgeonId(UUID v)                { s.surgeonId             = v; return this; }
        public Builder surgeonName(String v)            { s.surgeonName           = v; return this; }
        public Builder anesthetistId(UUID v)            { s.anesthetistId         = v; return this; }
        public Builder anesthetistName(String v)        { s.anesthetistName       = v; return this; }
        public Builder assistantNames(String v)         { s.assistantNames        = v; return this; }
        public Builder preOpDiagnosis(String v)         { s.preOpDiagnosis        = v; return this; }
        public Builder bloodRequestId(UUID v)           { s.bloodRequestId        = v; return this; }
        public Builder bloodRequestNumber(String v)     { s.bloodRequestNumber    = v; return this; }
        public Builder notes(String v)                  { s.notes                 = v; return this; }
        public OtSchedule build()                       { return s; }
    }
}
