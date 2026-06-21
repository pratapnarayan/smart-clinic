package com.smartclinic.modules.bloodbank.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "blood_requests",
    indexes = {
        @Index(name = "idx_blood_req_patient", columnList = "patient_id"),
        @Index(name = "idx_blood_req_status",  columnList = "status"),
        @Index(name = "idx_blood_req_date",    columnList = "request_date")
    }
)
public class BloodRequest extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "request_number", nullable = false, unique = true, length = 30)
    private String requestNumber;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "requested_by", length = 200)
    private String requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 10)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false, length = 30)
    private ComponentType componentType = ComponentType.WHOLE_BLOOD;

    @Column(name = "units_required", nullable = false)
    private int unitsRequired = 1;

    @Column(name = "units_issued", nullable = false)
    private int unitsIssued = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Urgency urgency = Urgency.ROUTINE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "required_by")
    private LocalDateTime requiredBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum Urgency       { ROUTINE, URGENT, EMERGENCY }
    public enum RequestStatus { PENDING, PARTIALLY_FULFILLED, FULFILLED, CANCELLED }

    protected BloodRequest() {}

    public UUID          getId()            { return id; }
    public String        getRequestNumber() { return requestNumber; }
    public LocalDate     getRequestDate()   { return requestDate; }
    public UUID          getPatientId()     { return patientId; }
    public String        getPatientName()   { return patientName; }
    public String        getRequestedBy()   { return requestedBy; }
    public BloodGroup    getBloodGroup()    { return bloodGroup; }
    public ComponentType getComponentType() { return componentType; }
    public int           getUnitsRequired() { return unitsRequired; }
    public int           getUnitsIssued()   { return unitsIssued; }
    public Urgency       getUrgency()       { return urgency; }
    public RequestStatus getStatus()        { return status; }
    public LocalDateTime getRequiredBy()    { return requiredBy; }
    public String        getNotes()         { return notes; }

    public void setUnitsIssued(int v)      { this.unitsIssued = v; }
    public void setStatus(RequestStatus v) { this.status      = v; }
    public void setNotes(String v)         { this.notes       = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final BloodRequest r = new BloodRequest();
        public Builder requestNumber(String v)       { r.requestNumber = v; return this; }
        public Builder requestDate(LocalDate v)      { r.requestDate   = v; return this; }
        public Builder patientId(UUID v)             { r.patientId     = v; return this; }
        public Builder patientName(String v)         { r.patientName   = v; return this; }
        public Builder requestedBy(String v)         { r.requestedBy   = v; return this; }
        public Builder bloodGroup(BloodGroup v)      { r.bloodGroup    = v; return this; }
        public Builder componentType(ComponentType v){ r.componentType = v; return this; }
        public Builder unitsRequired(int v)          { r.unitsRequired = v; return this; }
        public Builder urgency(Urgency v)            { r.urgency       = v; return this; }
        public Builder requiredBy(LocalDateTime v)   { r.requiredBy    = v; return this; }
        public Builder notes(String v)               { r.notes         = v; return this; }
        public BloodRequest build()                  { return r; }
    }
}
