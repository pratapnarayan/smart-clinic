package com.smarthospital.modules.inventory.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "stock_receipts",
    indexes = {
        @Index(name = "idx_receipts_item_id", columnList = "item_id"),
        @Index(name = "idx_receipts_date",    columnList = "entry_date")
    }
)
public class StockReceipt extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 30)
    private String receiptNumber;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "item_unit", nullable = false, length = 30)
    private String itemUnit;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_cost", precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "supplier_name", length = 200)
    private String supplierName;

    @Column(name = "grn_number", length = 100)
    private String grnNumber;

    @Column(name = "received_by", length = 200)
    private String receivedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    protected StockReceipt() {}

    public UUID       getId()            { return id; }
    public String     getReceiptNumber() { return receiptNumber; }
    public LocalDate  getEntryDate()     { return entryDate; }
    public UUID       getItemId()        { return itemId; }
    public String     getItemName()      { return itemName; }
    public String     getItemUnit()      { return itemUnit; }
    public int        getQuantity()      { return quantity; }
    public BigDecimal getUnitCost()      { return unitCost; }
    public BigDecimal getTotalCost()     { return totalCost; }
    public String     getSupplierName()  { return supplierName; }
    public String     getGrnNumber()     { return grnNumber; }
    public String     getReceivedBy()    { return receivedBy; }
    public String     getNotes()         { return notes; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final StockReceipt r = new StockReceipt();
        public Builder receiptNumber(String v)   { r.receiptNumber = v; return this; }
        public Builder entryDate(LocalDate v)    { r.entryDate     = v; return this; }
        public Builder itemId(UUID v)            { r.itemId        = v; return this; }
        public Builder itemName(String v)        { r.itemName      = v; return this; }
        public Builder itemUnit(String v)        { r.itemUnit      = v; return this; }
        public Builder quantity(int v)           { r.quantity      = v; return this; }
        public Builder unitCost(BigDecimal v)    { r.unitCost      = v; return this; }
        public Builder totalCost(BigDecimal v)   { r.totalCost     = v; return this; }
        public Builder supplierName(String v)    { r.supplierName  = v; return this; }
        public Builder grnNumber(String v)       { r.grnNumber     = v; return this; }
        public Builder receivedBy(String v)      { r.receivedBy    = v; return this; }
        public Builder notes(String v)           { r.notes         = v; return this; }
        public StockReceipt build()              { return r; }
    }
}
