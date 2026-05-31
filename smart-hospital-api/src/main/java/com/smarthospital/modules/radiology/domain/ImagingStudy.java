package com.smarthospital.modules.radiology.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "imaging_studies",
       indexes = @Index(name = "idx_studies_modality", columnList = "modality_id"))
public class ImagingStudy extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "modality_id", nullable = false)
    private UUID modalityId;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "prep_instructions", columnDefinition = "TEXT")
    private String prepInstructions;

    @Column(nullable = false)
    private boolean active = true;

    protected ImagingStudy() {}

    public UUID       getId()              { return id; }
    public String     getCode()            { return code; }
    public String     getName()            { return name; }
    public UUID       getModalityId()      { return modalityId; }
    public String     getDescription()     { return description; }
    public BigDecimal getPrice()           { return price; }
    public String     getPrepInstructions(){ return prepInstructions; }
    public boolean    isActive()           { return active; }

    public void setCode(String v)             { this.code             = v; }
    public void setName(String v)             { this.name             = v; }
    public void setModalityId(UUID v)         { this.modalityId       = v; }
    public void setDescription(String v)      { this.description      = v; }
    public void setPrice(BigDecimal v)        { this.price            = v; }
    public void setPrepInstructions(String v) { this.prepInstructions = v; }
    public void setActive(boolean v)          { this.active           = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final ImagingStudy s = new ImagingStudy();
        public Builder code(String v)             { s.code             = v; return this; }
        public Builder name(String v)             { s.name             = v; return this; }
        public Builder modalityId(UUID v)         { s.modalityId       = v; return this; }
        public Builder description(String v)      { s.description      = v; return this; }
        public Builder price(BigDecimal v)        { s.price            = v; return this; }
        public Builder prepInstructions(String v) { s.prepInstructions = v; return this; }
        public ImagingStudy build()               { return s; }
    }
}
