package com.smartclinic.modules.pharmacy.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "medicines", indexes = {
        @Index(name = "idx_medicines_category", columnList = "category_id")
})
@SQLDelete(sql = "UPDATE medicines SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Medicine extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private MedicineCategory category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "generic_name", length = 200)
    private String genericName;

    /** Dispensing unit: TAB, CAP, ML, MG, VIAL, etc. */
    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "reorder_level", nullable = false)
    private int reorderLevel = 10;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected Medicine() {}

    // Getters
    public UUID             getId()           { return id; }
    public MedicineCategory getCategory()     { return category; }
    public String           getName()         { return name; }
    public String           getGenericName()  { return genericName; }
    public String           getUnit()         { return unit; }
    public int              getReorderLevel() { return reorderLevel; }
    public Instant          getDeletedAt()    { return deletedAt; }

    // Setters
    public void setCategory(MedicineCategory v)  { this.category     = v; }
    public void setName(String v)                { this.name         = v; }
    public void setGenericName(String v)         { this.genericName  = v; }
    public void setUnit(String v)                { this.unit         = v; }
    public void setReorderLevel(int v)           { this.reorderLevel = v; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final Medicine m = new Medicine();
        public Builder category(MedicineCategory v) { m.category     = v; return this; }
        public Builder name(String v)               { m.name         = v; return this; }
        public Builder genericName(String v)        { m.genericName  = v; return this; }
        public Builder unit(String v)               { m.unit         = v; return this; }
        public Builder reorderLevel(int v)          { m.reorderLevel = v; return this; }
        public Medicine build()                     { return m; }
    }
}
