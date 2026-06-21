package com.smartclinic.modules.ipd.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "beds",
       uniqueConstraints = @UniqueConstraint(name = "uq_bed_ward_number",
                                             columnNames = {"ward_id", "bed_number"}))
public class IpdBed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    @Column(name = "bed_number", nullable = false, length = 20)
    private String bedNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", nullable = false, length = 50)
    private BedType bedType = BedType.GENERAL;

    @Column(name = "daily_charge", precision = 10, scale = 2)
    private BigDecimal dailyCharge = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BedStatus status = BedStatus.AVAILABLE;

    public enum BedType   { GENERAL, PRIVATE, ICU, SEMI_PRIVATE }
    public enum BedStatus { AVAILABLE, OCCUPIED, MAINTENANCE }

    protected IpdBed() {}

    public UUID       getId()          { return id; }
    public UUID       getWardId()      { return wardId; }
    public String     getBedNumber()   { return bedNumber; }
    public BedType    getBedType()     { return bedType; }
    public BigDecimal getDailyCharge() { return dailyCharge; }
    public BedStatus  getStatus()      { return status; }

    public void setStatus(BedStatus v)      { this.status      = v; }
    public void setDailyCharge(BigDecimal v){ this.dailyCharge  = v; }
    public void setBedType(BedType v)       { this.bedType      = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final IpdBed b = new IpdBed();
        public Builder wardId(UUID v)          { b.wardId       = v; return this; }
        public Builder bedNumber(String v)     { b.bedNumber    = v; return this; }
        public Builder bedType(BedType v)      { b.bedType      = v; return this; }
        public Builder dailyCharge(BigDecimal v){ b.dailyCharge = v; return this; }
        public IpdBed build()                  { return b; }
    }
}
