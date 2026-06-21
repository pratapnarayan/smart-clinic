package com.smartclinic.modules.bloodbank.domain;

import com.smartclinic.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "blood_issues",
    indexes = {
        @Index(name = "idx_blood_issues_request", columnList = "request_id"),
        @Index(name = "idx_blood_issues_date",    columnList = "issue_date")
    }
)
public class BloodIssue extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "issue_number", nullable = false, unique = true, length = 30)
    private String issueNumber;

    @Column(name = "issue_date", nullable = false)
    private Instant issueDate;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    /** Snapshot so the issue record survives request deletion */
    @Column(name = "request_number", nullable = false, length = 30)
    private String requestNumber;

    @Column(name = "unit_id", nullable = false, unique = true)
    private UUID unitId;

    @Column(name = "unit_number", nullable = false, length = 30)
    private String unitNumber;

    @Column(name = "blood_group", nullable = false, length = 10)
    private String bloodGroup;

    @Column(name = "component_type", nullable = false, length = 30)
    private String componentType;

    @Column(name = "issued_to", nullable = false, length = 200)
    private String issuedTo;

    @Column(name = "issued_by", length = 200)
    private String issuedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    protected BloodIssue() {}

    public UUID    getId()            { return id; }
    public String  getIssueNumber()   { return issueNumber; }
    public Instant getIssueDate()     { return issueDate; }
    public UUID    getRequestId()     { return requestId; }
    public String  getRequestNumber() { return requestNumber; }
    public UUID    getUnitId()        { return unitId; }
    public String  getUnitNumber()    { return unitNumber; }
    public String  getBloodGroup()    { return bloodGroup; }
    public String  getComponentType() { return componentType; }
    public String  getIssuedTo()      { return issuedTo; }
    public String  getIssuedBy()      { return issuedBy; }
    public String  getNotes()         { return notes; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final BloodIssue i = new BloodIssue();
        public Builder issueNumber(String v)   { i.issueNumber   = v; return this; }
        public Builder issueDate(Instant v)    { i.issueDate     = v; return this; }
        public Builder requestId(UUID v)       { i.requestId     = v; return this; }
        public Builder requestNumber(String v) { i.requestNumber = v; return this; }
        public Builder unitId(UUID v)          { i.unitId        = v; return this; }
        public Builder unitNumber(String v)    { i.unitNumber    = v; return this; }
        public Builder bloodGroup(String v)    { i.bloodGroup    = v; return this; }
        public Builder componentType(String v) { i.componentType = v; return this; }
        public Builder issuedTo(String v)      { i.issuedTo      = v; return this; }
        public Builder issuedBy(String v)      { i.issuedBy      = v; return this; }
        public Builder notes(String v)         { i.notes         = v; return this; }
        public BloodIssue build()              { return i; }
    }
}
