package com.smarthospital.modules.pharmacy.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pharmacy bill — immutable once created.
 * Extends CreatedOnlyAuditEntity (no updated_at / updated_by columns in schema).
 *
 * patientId is nullable: counter sale (OTC) has no patient.
 * bill_items use denormalised medicine_name so history is never lost
 * even if the batch is later deleted (fixes the legacy INNER JOIN data-loss bug).
 */
@Entity
@Table(name = "pharmacy_bills", indexes = {
        @Index(name = "idx_pharmacy_bills_patient",
               columnList = "patient_id")
})
public class PharmacyBill extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Nullable — OTC sales don't require a registered patient */
    @Column(name = "patient_id")
    private UUID patientId;

    /** Snapshot of patient name at billing time */
    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Column(name = "bill_number", nullable = false, unique = true, length = 20)
    private String billNumber;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "net_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Column(name = "payment_mode", nullable = false, length = 20)
    private String paymentMode = "CASH";

    @Column(nullable = false, length = 20)
    private String status = "PAID";

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PharmacyBillItem> items = new ArrayList<>();

    protected PharmacyBill() {}

    // Getters
    public UUID                   getId()          { return id; }
    public UUID                   getPatientId()   { return patientId; }
    public String                 getPatientName() { return patientName; }
    public String                 getBillNumber()  { return billNumber; }
    public BigDecimal             getTotalAmount() { return totalAmount; }
    public BigDecimal             getDiscount()    { return discount; }
    public BigDecimal             getNetAmount()   { return netAmount; }
    public String                 getPaymentMode() { return paymentMode; }
    public String                 getStatus()      { return status; }
    public List<PharmacyBillItem> getItems()       { return items; }

    // Setters used during construction only
    public void setPatientId(UUID v)       { this.patientId   = v; }
    public void setPatientName(String v)   { this.patientName = v; }
    public void setBillNumber(String v)    { this.billNumber  = v; }
    public void setPaymentMode(String v)   { this.paymentMode = v; }
    public void setDiscount(BigDecimal v)  { this.discount    = v; }

    public void addItem(PharmacyBillItem item) {
        items.add(item);
        item.setBill(this);
    }

    /** Recalculates totalAmount and netAmount from items + discount. */
    public void recalculateTotals() {
        this.totalAmount = items.stream()
                .map(PharmacyBillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.netAmount = totalAmount.subtract(discount);
    }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final PharmacyBill b = new PharmacyBill();
        public Builder patientId(UUID v)      { b.patientId   = v; return this; }
        public Builder patientName(String v)  { b.patientName = v; return this; }
        public Builder billNumber(String v)   { b.billNumber  = v; return this; }
        public Builder paymentMode(String v)  { b.paymentMode = v; return this; }
        public Builder discount(BigDecimal v) { b.discount    = v; return this; }
        public PharmacyBill build()           { return b; }
    }
}
