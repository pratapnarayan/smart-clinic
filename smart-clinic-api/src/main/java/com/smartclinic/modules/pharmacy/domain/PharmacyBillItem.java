package com.smartclinic.modules.pharmacy.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One line item on a pharmacy bill.
 *
 * batch_id is nullable (ON DELETE SET NULL in schema) so the historical
 * bill record is preserved even if the batch is later deleted — this
 * directly fixes the legacy INNER JOIN data-loss bug.
 *
 * medicine_name is denormalised for the same reason.
 */
@Entity
@Table(name = "pharmacy_bill_items")
public class PharmacyBillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private PharmacyBill bill;

    /** Nullable — set to NULL if batch is deleted; bill item survives. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private MedicineBatch batch;

    /** Denormalised snapshot — never null even after batch deletion. */
    @Column(name = "medicine_name", nullable = false, length = 200)
    private String medicineName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    protected PharmacyBillItem() {}

    public PharmacyBillItem(MedicineBatch batch, String medicineName,
                             int quantity, BigDecimal unitPrice) {
        this.batch        = batch;
        this.medicineName = medicineName;
        this.quantity     = quantity;
        this.unitPrice    = unitPrice;
        this.totalPrice   = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID          getId()           { return id; }
    public PharmacyBill  getBill()         { return bill; }
    public MedicineBatch getBatch()        { return batch; }
    public String        getMedicineName() { return medicineName; }
    public int           getQuantity()     { return quantity; }
    public BigDecimal    getUnitPrice()    { return unitPrice; }
    public BigDecimal    getTotalPrice()   { return totalPrice; }

    void setBill(PharmacyBill v) { this.bill = v; }
}
