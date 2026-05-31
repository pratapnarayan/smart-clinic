package com.smarthospital.modules.radiology.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "radiology_order_items",
       indexes = @Index(name = "idx_rad_items_order_id", columnList = "order_id"))
public class RadiologyOrderItem extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private RadiologyOrder order;

    @Column(name = "study_id", nullable = false)
    private UUID studyId;

    @Column(name = "study_code", nullable = false, length = 30)
    private String studyCode;

    @Column(name = "study_name", nullable = false, length = 200)
    private String studyName;

    @Column(name = "modality_name", nullable = false, length = 100)
    private String modalityName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "prep_instructions", columnDefinition = "TEXT")
    private String prepInstructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemStatus status = ItemStatus.PENDING;

    /** Detailed radiological observations */
    @Column(columnDefinition = "TEXT")
    private String findings;

    /** Summary / diagnostic conclusion */
    @Column(columnDefinition = "TEXT")
    private String impression;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "reported_by", length = 200)
    private String reportedBy;

    public enum ItemStatus { PENDING, IN_PROGRESS, REPORTED }

    protected RadiologyOrderItem() {}

    public RadiologyOrderItem(RadiologyOrder order, ImagingStudy study, String modalityName) {
        this.order            = order;
        this.studyId          = study.getId();
        this.studyCode        = study.getCode();
        this.studyName        = study.getName();
        this.modalityName     = modalityName;
        this.price            = study.getPrice();
        this.prepInstructions = study.getPrepInstructions();
    }

    public UUID          getId()              { return id; }
    public RadiologyOrder getOrder()          { return order; }
    public UUID          getStudyId()         { return studyId; }
    public String        getStudyCode()       { return studyCode; }
    public String        getStudyName()       { return studyName; }
    public String        getModalityName()    { return modalityName; }
    public BigDecimal    getPrice()           { return price; }
    public String        getPrepInstructions(){ return prepInstructions; }
    public ItemStatus    getStatus()          { return status; }
    public String        getFindings()        { return findings; }
    public String        getImpression()      { return impression; }
    public LocalDateTime getReportedAt()      { return reportedAt; }
    public String        getReportedBy()      { return reportedBy; }

    public void setStatus(ItemStatus v)         { this.status     = v; }
    public void setFindings(String v)           { this.findings   = v; }
    public void setImpression(String v)         { this.impression = v; }
    public void setReportedAt(LocalDateTime v)  { this.reportedAt = v; }
    public void setReportedBy(String v)         { this.reportedBy = v; }
}
