package com.smarthospital.modules.finance.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "expense_entries",
    indexes = {
        @Index(name = "idx_expense_date",     columnList = "entry_date"),
        @Index(name = "idx_expense_category", columnList = "category_id")
    }
)
public class ExpenseEntry extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entry_number", nullable = false, unique = true, length = 30)
    private String entryNumber;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    /** Snapshot so history survives category renames */
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false, length = 20)
    private PaymentMode paymentMode = PaymentMode.CASH;

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    @Column(name = "paid_to", length = 200)
    private String paidTo;

    @Column(name = "approved_by", length = 200)
    private String approvedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum PaymentMode { CASH, CARD, UPI, CHEQUE, NEFT, OTHER }

    protected ExpenseEntry() {}

    public UUID        getId()           { return id; }
    public String      getEntryNumber()  { return entryNumber; }
    public LocalDate   getEntryDate()    { return entryDate; }
    public UUID        getCategoryId()   { return categoryId; }
    public String      getCategoryName() { return categoryName; }
    public String      getDescription()  { return description; }
    public BigDecimal  getAmount()       { return amount; }
    public PaymentMode getPaymentMode()  { return paymentMode; }
    public String      getReferenceNo()  { return referenceNo; }
    public String      getPaidTo()       { return paidTo; }
    public String      getApprovedBy()   { return approvedBy; }
    public String      getNotes()        { return notes; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final ExpenseEntry e = new ExpenseEntry();
        public Builder entryNumber(String v)     { e.entryNumber  = v; return this; }
        public Builder entryDate(LocalDate v)    { e.entryDate    = v; return this; }
        public Builder categoryId(UUID v)        { e.categoryId   = v; return this; }
        public Builder categoryName(String v)    { e.categoryName = v; return this; }
        public Builder description(String v)     { e.description  = v; return this; }
        public Builder amount(BigDecimal v)      { e.amount       = v; return this; }
        public Builder paymentMode(PaymentMode v){ e.paymentMode  = v; return this; }
        public Builder referenceNo(String v)     { e.referenceNo  = v; return this; }
        public Builder paidTo(String v)          { e.paidTo       = v; return this; }
        public Builder approvedBy(String v)      { e.approvedBy   = v; return this; }
        public Builder notes(String v)           { e.notes        = v; return this; }
        public ExpenseEntry build()              { return e; }
    }
}
