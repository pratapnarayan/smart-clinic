package com.smarthospital.modules.pharmacy.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "medicine_batches", indexes = {
        @Index(name = "idx_medicine_batches_expiry",    columnList = "expiry_date"),
        @Index(name = "idx_medicine_batches_medicine",  columnList = "medicine_id")
})
public class MedicineBatch extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "batch_number", nullable = false, length = 50)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    protected MedicineBatch() {}

    // Getters
    public UUID       getId()            { return id; }
    public Medicine   getMedicine()      { return medicine; }
    public String     getBatchNumber()   { return batchNumber; }
    public LocalDate  getExpiryDate()    { return expiryDate; }
    public int        getQuantity()      { return quantity; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public BigDecimal getSalePrice()     { return salePrice; }

    // Setters
    public void setQuantity(int v)             { this.quantity      = v; }
    public void setPurchasePrice(BigDecimal v) { this.purchasePrice = v; }
    public void setSalePrice(BigDecimal v)     { this.salePrice     = v; }
    public void setExpiryDate(LocalDate v)     { this.expiryDate    = v; }

    /** Deducts qty from stock; throws IllegalStateException if insufficient. */
    public void deductStock(int qty) {
        if (this.quantity < qty) {
            throw new IllegalStateException(
                    "Insufficient stock for batch " + batchNumber +
                    ": available=" + this.quantity + ", requested=" + qty);
        }
        this.quantity -= qty;
    }

    public boolean isExpired()    { return expiryDate.isBefore(LocalDate.now()); }
    public boolean isLowStock(int reorderLevel) { return quantity <= reorderLevel; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final MedicineBatch b = new MedicineBatch();
        public Builder medicine(Medicine v)      { b.medicine      = v; return this; }
        public Builder batchNumber(String v)     { b.batchNumber   = v; return this; }
        public Builder expiryDate(LocalDate v)   { b.expiryDate    = v; return this; }
        public Builder quantity(int v)           { b.quantity      = v; return this; }
        public Builder purchasePrice(BigDecimal v){ b.purchasePrice = v; return this; }
        public Builder salePrice(BigDecimal v)   { b.salePrice     = v; return this; }
        public MedicineBatch build()             { return b; }
    }
}
