package com.smarthospital.modules.pathology.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lab_tests",
       indexes = @Index(name = "idx_lab_tests_category", columnList = "category_id"))
public class LabTest extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "turnaround_hours", nullable = false)
    private int turnaroundHours = 24;

    @Column(length = 50)
    private String unit;

    @Column(name = "normal_range", length = 200)
    private String normalRange;

    @Column(nullable = false)
    private boolean active = true;

    protected LabTest() {}

    public UUID       getId()              { return id; }
    public String     getCode()            { return code; }
    public String     getName()            { return name; }
    public UUID       getCategoryId()      { return categoryId; }
    public String     getDescription()     { return description; }
    public BigDecimal getPrice()           { return price; }
    public int        getTurnaroundHours() { return turnaroundHours; }
    public String     getUnit()            { return unit; }
    public String     getNormalRange()     { return normalRange; }
    public boolean    isActive()           { return active; }

    public void setCode(String v)            { this.code            = v; }
    public void setName(String v)            { this.name            = v; }
    public void setCategoryId(UUID v)        { this.categoryId      = v; }
    public void setDescription(String v)     { this.description     = v; }
    public void setPrice(BigDecimal v)       { this.price           = v; }
    public void setTurnaroundHours(int v)    { this.turnaroundHours = v; }
    public void setUnit(String v)            { this.unit            = v; }
    public void setNormalRange(String v)     { this.normalRange     = v; }
    public void setActive(boolean v)         { this.active          = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final LabTest t = new LabTest();
        public Builder code(String v)            { t.code            = v; return this; }
        public Builder name(String v)            { t.name            = v; return this; }
        public Builder categoryId(UUID v)        { t.categoryId      = v; return this; }
        public Builder description(String v)     { t.description     = v; return this; }
        public Builder price(BigDecimal v)       { t.price           = v; return this; }
        public Builder turnaroundHours(int v)    { t.turnaroundHours = v; return this; }
        public Builder unit(String v)            { t.unit            = v; return this; }
        public Builder normalRange(String v)     { t.normalRange     = v; return this; }
        public LabTest build()                   { return t; }
    }
}
