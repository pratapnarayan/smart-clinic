package com.smarthospital.modules.bloodbank.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "blood_donors",
    indexes = {
        @Index(name = "idx_donors_blood_group", columnList = "blood_group"),
        @Index(name = "idx_donors_mobile",      columnList = "mobile")
    }
)
public class BloodDonor extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "donor_number", nullable = false, unique = true, length = 30)
    private String donorNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 10)
    private BloodGroup bloodGroup;

    @Column(length = 15)
    private String mobile;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "last_donation_date")
    private LocalDate lastDonationDate;

    @Column(name = "total_donations", nullable = false)
    private int totalDonations = 0;

    @Column(nullable = false)
    private boolean active = true;

    public enum Gender { MALE, FEMALE, OTHER }

    protected BloodDonor() {}

    public UUID       getId()               { return id; }
    public String     getDonorNumber()      { return donorNumber; }
    public String     getFirstName()        { return firstName; }
    public String     getLastName()         { return lastName; }
    public Gender     getGender()           { return gender; }
    public LocalDate  getDateOfBirth()      { return dateOfBirth; }
    public BloodGroup getBloodGroup()       { return bloodGroup; }
    public String     getMobile()           { return mobile; }
    public String     getEmail()            { return email; }
    public String     getAddress()          { return address; }
    public LocalDate  getLastDonationDate() { return lastDonationDate; }
    public int        getTotalDonations()   { return totalDonations; }
    public boolean    isActive()            { return active; }

    public void setFirstName(String v)           { this.firstName        = v; }
    public void setLastName(String v)            { this.lastName         = v; }
    public void setGender(Gender v)              { this.gender           = v; }
    public void setDateOfBirth(LocalDate v)      { this.dateOfBirth      = v; }
    public void setBloodGroup(BloodGroup v)      { this.bloodGroup       = v; }
    public void setMobile(String v)              { this.mobile           = v; }
    public void setEmail(String v)               { this.email            = v; }
    public void setAddress(String v)             { this.address          = v; }
    public void setLastDonationDate(LocalDate v) { this.lastDonationDate = v; }
    public void setTotalDonations(int v)         { this.totalDonations   = v; }
    public void setActive(boolean v)             { this.active           = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final BloodDonor d = new BloodDonor();
        public Builder donorNumber(String v)      { d.donorNumber  = v; return this; }
        public Builder firstName(String v)        { d.firstName    = v; return this; }
        public Builder lastName(String v)         { d.lastName     = v; return this; }
        public Builder gender(Gender v)           { d.gender       = v; return this; }
        public Builder dateOfBirth(LocalDate v)   { d.dateOfBirth  = v; return this; }
        public Builder bloodGroup(BloodGroup v)   { d.bloodGroup   = v; return this; }
        public Builder mobile(String v)           { d.mobile       = v; return this; }
        public Builder email(String v)            { d.email        = v; return this; }
        public Builder address(String v)          { d.address      = v; return this; }
        public BloodDonor build()                 { return d; }
    }
}
