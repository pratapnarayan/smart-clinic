package com.smarthospital.modules.ipd.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "wards")
public class IpdWard extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ward_type", nullable = false, length = 50)
    private WardType wardType = WardType.GENERAL;

    @Column(name = "total_beds", nullable = false)
    private int totalBeds = 0;

    @Column(nullable = false)
    private boolean active = true;

    public enum WardType {
        GENERAL, ICU, NICU, MATERNITY, SURGERY, PEDIATRIC, ORTHOPEDIC, PRIVATE
    }

    protected IpdWard() {}

    public UUID     getId()       { return id; }
    public String   getName()     { return name; }
    public WardType getWardType() { return wardType; }
    public int      getTotalBeds(){ return totalBeds; }
    public boolean  isActive()    { return active; }

    public void setName(String v)      { this.name      = v; }
    public void setWardType(WardType v){ this.wardType   = v; }
    public void setTotalBeds(int v)    { this.totalBeds  = v; }
    public void setActive(boolean v)   { this.active     = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final IpdWard w = new IpdWard();
        public Builder name(String v)      { w.name      = v; return this; }
        public Builder wardType(WardType v){ w.wardType   = v; return this; }
        public Builder totalBeds(int v)    { w.totalBeds  = v; return this; }
        public Builder active(boolean v)   { w.active     = v; return this; }
        public IpdWard build()             { return w; }
    }
}
