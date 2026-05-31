package com.smarthospital.modules.patient.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patient_mobile", columnList = "mobile"),
        @Index(name = "idx_patient_legacy_id", columnList = "legacy_id")
})
@SQLDelete(sql = "UPDATE patients SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Patient extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(length = 15)
    private String mobile;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 10)
    private String bloodGroup;

    @Column(length = 100)
    private String guardianName;

    @Column(length = 15)
    private String guardianMobile;

    @Column(length = 500)
    private String photoUrl;

    /** Preserved during parallel migration run — maps to legacy MariaDB patient ID */
    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(columnDefinition = "tsvector", insertable = false, updatable = false)
    private String searchVector;

    private Instant deletedAt;

    public enum Gender { MALE, FEMALE, OTHER }

    // Required by JPA
    protected Patient() {}

    // Getters
    public UUID      getId()             { return id; }
    public String    getFirstName()      { return firstName; }
    public String    getLastName()       { return lastName; }
    public LocalDate getDateOfBirth()    { return dateOfBirth; }
    public Gender    getGender()         { return gender; }
    public String    getMobile()         { return mobile; }
    public String    getEmail()          { return email; }
    public String    getAddress()        { return address; }
    public String    getBloodGroup()     { return bloodGroup; }
    public String    getGuardianName()   { return guardianName; }
    public String    getGuardianMobile() { return guardianMobile; }
    public String    getPhotoUrl()       { return photoUrl; }
    public Long      getLegacyId()       { return legacyId; }
    public Instant   getDeletedAt()      { return deletedAt; }

    // Setters
    public void setFirstName(String v)      { this.firstName = v; }
    public void setLastName(String v)       { this.lastName = v; }
    public void setDateOfBirth(LocalDate v) { this.dateOfBirth = v; }
    public void setGender(Gender v)         { this.gender = v; }
    public void setMobile(String v)         { this.mobile = v; }
    public void setEmail(String v)          { this.email = v; }
    public void setAddress(String v)        { this.address = v; }
    public void setBloodGroup(String v)     { this.bloodGroup = v; }
    public void setGuardianName(String v)   { this.guardianName = v; }
    public void setGuardianMobile(String v) { this.guardianMobile = v; }
    public void setPhotoUrl(String v)       { this.photoUrl = v; }
    public void setLegacyId(Long v)         { this.legacyId = v; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final Patient p = new Patient();
        public Builder firstName(String v)      { p.firstName = v;      return this; }
        public Builder lastName(String v)       { p.lastName = v;       return this; }
        public Builder dateOfBirth(LocalDate v) { p.dateOfBirth = v;    return this; }
        public Builder gender(Gender v)         { p.gender = v;         return this; }
        public Builder mobile(String v)         { p.mobile = v;         return this; }
        public Builder email(String v)          { p.email = v;          return this; }
        public Builder address(String v)        { p.address = v;        return this; }
        public Builder bloodGroup(String v)     { p.bloodGroup = v;     return this; }
        public Builder guardianName(String v)   { p.guardianName = v;   return this; }
        public Builder guardianMobile(String v) { p.guardianMobile = v; return this; }
        public Builder photoUrl(String v)       { p.photoUrl = v;       return this; }
        public Builder legacyId(Long v)         { p.legacyId = v;       return this; }
        public Patient build()                  { return p; }
    }
}
