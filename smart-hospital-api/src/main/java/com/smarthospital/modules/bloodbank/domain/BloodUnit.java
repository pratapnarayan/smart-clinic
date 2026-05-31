package com.smarthospital.modules.bloodbank.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "blood_units",
    indexes = {
        @Index(name = "idx_units_group_status", columnList = "blood_group, status"),
        @Index(name = "idx_units_status",       columnList = "status"),
        @Index(name = "idx_units_expiry",       columnList = "expiry_date")
    }
)
public class BloodUnit extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "unit_number", nullable = false, unique = true, length = 30)
    private String unitNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 10)
    private BloodGroup bloodGroup;

    @Column(name = "donor_id")
    private UUID donorId;

    @Column(name = "donor_name", length = 200)
    private String donorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false, length = 30)
    private ComponentType componentType = ComponentType.WHOLE_BLOOD;

    @Column(name = "volume_ml", nullable = false)
    private int volumeMl = 450;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "testing_status", nullable = false, length = 20)
    private TestingStatus testingStatus = TestingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitStatus status = UnitStatus.PENDING_TESTING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum TestingStatus { PENDING, CLEARED, REJECTED }
    public enum UnitStatus    { PENDING_TESTING, AVAILABLE, RESERVED, ISSUED, DISCARDED, EXPIRED }

    protected BloodUnit() {}

    public UUID          getId()             { return id; }
    public String        getUnitNumber()     { return unitNumber; }
    public BloodGroup    getBloodGroup()     { return bloodGroup; }
    public UUID          getDonorId()        { return donorId; }
    public String        getDonorName()      { return donorName; }
    public ComponentType getComponentType()  { return componentType; }
    public int           getVolumeMl()       { return volumeMl; }
    public LocalDate     getCollectionDate() { return collectionDate; }
    public LocalDate     getExpiryDate()     { return expiryDate; }
    public TestingStatus getTestingStatus()  { return testingStatus; }
    public UnitStatus    getStatus()         { return status; }
    public String        getNotes()          { return notes; }
    public boolean       isExpired()         { return expiryDate.isBefore(LocalDate.now()); }

    public void setTestingStatus(TestingStatus v) { this.testingStatus = v; }
    public void setStatus(UnitStatus v)           { this.status        = v; }
    public void setNotes(String v)                { this.notes         = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final BloodUnit u = new BloodUnit();
        public Builder unitNumber(String v)       { u.unitNumber     = v; return this; }
        public Builder bloodGroup(BloodGroup v)   { u.bloodGroup     = v; return this; }
        public Builder donorId(UUID v)            { u.donorId        = v; return this; }
        public Builder donorName(String v)        { u.donorName      = v; return this; }
        public Builder componentType(ComponentType v){ u.componentType = v; return this; }
        public Builder volumeMl(int v)            { u.volumeMl       = v; return this; }
        public Builder collectionDate(LocalDate v){ u.collectionDate = v; return this; }
        public Builder expiryDate(LocalDate v)    { u.expiryDate     = v; return this; }
        public Builder testingStatus(TestingStatus v){ u.testingStatus = v; return this; }
        public Builder status(UnitStatus v)       { u.status         = v; return this; }
        public Builder notes(String v)            { u.notes          = v; return this; }
        public BloodUnit build()                  { return u; }
    }
}
