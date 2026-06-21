package com.smartclinic.modules.radiology.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "radiology_orders",
    indexes = {
        @Index(name = "idx_rad_orders_patient_id", columnList = "patient_id"),
        @Index(name = "idx_rad_orders_scheduled",  columnList = "scheduled_at")
    }
)
public class RadiologyOrder extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "patient_mobile", length = 15)
    private String patientMobile;

    @Column(name = "referred_by_id")
    private UUID referredById;

    @Column(name = "referred_by_name", length = 200)
    private String referredByName;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceType sourceType = SourceType.WALK_IN;

    @Column(name = "source_id")
    private UUID sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.ROUTINE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "clinical_history", columnDefinition = "TEXT")
    private String clinicalHistory;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RadiologyOrderItem> items = new ArrayList<>();

    public enum SourceType    { OPD, IPD, WALK_IN }
    public enum Priority      { ROUTINE, URGENT, STAT }
    public enum OrderStatus   { PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED }
    public enum PaymentStatus { PENDING, PAID, PARTIAL, WAIVED }

    protected RadiologyOrder() {}

    public UUID          getId()              { return id; }
    public String        getOrderNumber()     { return orderNumber; }
    public UUID          getPatientId()       { return patientId; }
    public String        getPatientName()     { return patientName; }
    public String        getPatientMobile()   { return patientMobile; }
    public UUID          getReferredById()    { return referredById; }
    public String        getReferredByName()  { return referredByName; }
    public SourceType    getSourceType()      { return sourceType; }
    public UUID          getSourceId()        { return sourceId; }
    public Priority      getPriority()        { return priority; }
    public OrderStatus   getStatus()          { return status; }
    public LocalDateTime getScheduledAt()     { return scheduledAt; }
    public String        getClinicalHistory() { return clinicalHistory; }
    public BigDecimal    getTotalAmount()     { return totalAmount; }
    public BigDecimal    getDiscount()        { return discount; }
    public BigDecimal    getNetAmount()       { return netAmount; }
    public PaymentStatus getPaymentStatus()   { return paymentStatus; }
    public String        getNotes()           { return notes; }
    public List<RadiologyOrderItem> getItems(){ return items; }

    public void setStatus(OrderStatus v)          { this.status          = v; }
    public void setScheduledAt(LocalDateTime v)   { this.scheduledAt     = v; }
    public void setClinicalHistory(String v)      { this.clinicalHistory = v; }
    public void setDiscount(BigDecimal v)         { this.discount        = v; }
    public void setPaymentStatus(PaymentStatus v) { this.paymentStatus   = v; }
    public void setNotes(String v)                { this.notes           = v; }

    public void recalculateTotals() {
        this.totalAmount = items.stream()
                .map(RadiologyOrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.netAmount = totalAmount.subtract(discount);
    }

    public void syncStatusFromItems() {
        if (items.isEmpty()) return;
        boolean allReported = items.stream()
                .allMatch(i -> i.getStatus() == RadiologyOrderItem.ItemStatus.REPORTED);
        if (allReported && status == OrderStatus.IN_PROGRESS) {
            this.status = OrderStatus.COMPLETED;
        }
    }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final RadiologyOrder o = new RadiologyOrder();
        public Builder orderNumber(String v)       { o.orderNumber     = v; return this; }
        public Builder patientId(UUID v)           { o.patientId       = v; return this; }
        public Builder patientName(String v)       { o.patientName     = v; return this; }
        public Builder patientMobile(String v)     { o.patientMobile   = v; return this; }
        public Builder referredById(UUID v)        { o.referredById    = v; return this; }
        public Builder referredByName(String v)    { o.referredByName  = v; return this; }
        public Builder sourceType(SourceType v)    { o.sourceType      = v; return this; }
        public Builder sourceId(UUID v)            { o.sourceId        = v; return this; }
        public Builder priority(Priority v)        { o.priority        = v; return this; }
        public Builder scheduledAt(LocalDateTime v){ o.scheduledAt     = v; return this; }
        public Builder clinicalHistory(String v)   { o.clinicalHistory = v; return this; }
        public Builder notes(String v)             { o.notes           = v; return this; }
        public RadiologyOrder build()              { return o; }
    }
}
