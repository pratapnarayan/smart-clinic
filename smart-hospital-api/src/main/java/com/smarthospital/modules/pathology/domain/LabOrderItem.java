package com.smarthospital.modules.pathology.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lab_order_items",
       indexes = @Index(name = "idx_lab_items_order_id", columnList = "order_id"))
public class LabOrderItem extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder order;

    /** UUID reference — no FK cross-module; snapshot fields below keep history intact */
    @Column(name = "test_id", nullable = false)
    private UUID testId;

    @Column(name = "test_code", nullable = false, length = 30)
    private String testCode;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(length = 50)
    private String unit;

    @Column(name = "normal_range", length = 200)
    private String normalRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemStatus status = ItemStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(name = "result_note", columnDefinition = "TEXT")
    private String resultNote;

    @Column(name = "result_entered_at")
    private LocalDateTime resultEnteredAt;

    @Column(name = "result_entered_by", length = 200)
    private String resultEnteredBy;

    public enum ItemStatus { PENDING, IN_PROGRESS, COMPLETED }

    protected LabOrderItem() {}

    public LabOrderItem(LabOrder order, LabTest test) {
        this.order       = order;
        this.testId      = test.getId();
        this.testCode    = test.getCode();
        this.testName    = test.getName();
        this.price       = test.getPrice();
        this.unit        = test.getUnit();
        this.normalRange = test.getNormalRange();
    }

    public UUID          getId()              { return id; }
    public LabOrder      getOrder()           { return order; }
    public UUID          getTestId()          { return testId; }
    public String        getTestCode()        { return testCode; }
    public String        getTestName()        { return testName; }
    public BigDecimal    getPrice()           { return price; }
    public String        getUnit()            { return unit; }
    public String        getNormalRange()     { return normalRange; }
    public ItemStatus    getStatus()          { return status; }
    public String        getResult()          { return result; }
    public String        getResultNote()      { return resultNote; }
    public LocalDateTime getResultEnteredAt() { return resultEnteredAt; }
    public String        getResultEnteredBy() { return resultEnteredBy; }

    public void setStatus(ItemStatus v)             { this.status          = v; }
    public void setResult(String v)                 { this.result          = v; }
    public void setResultNote(String v)             { this.resultNote      = v; }
    public void setResultEnteredAt(LocalDateTime v) { this.resultEnteredAt = v; }
    public void setResultEnteredBy(String v)        { this.resultEnteredBy = v; }
}
