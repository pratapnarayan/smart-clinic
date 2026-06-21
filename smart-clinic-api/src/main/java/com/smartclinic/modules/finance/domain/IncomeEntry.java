package com.smartclinic.modules.finance.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "income_entries",
    indexes = {
        @Index(name = "idx_income_date",        columnList = "entry_date"),
        @Index(name = "idx_income_source_type", columnList = "source_type")
    }
)
public class IncomeEntry extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entry_number", nullable = false, unique = true, length = 30)
    private String entryNumber;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceType sourceType = SourceType.OTHER;

    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false, length = 20)
    private PaymentMode paymentMode = PaymentMode.CASH;

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    @Column(name = "received_by", length = 200)
    private String receivedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum SourceType  { OPD, IPD, PHARMACY, PATHOLOGY, RADIOLOGY, OTHER }
    public enum PaymentMode { CASH, CARD, UPI, CHEQUE, NEFT, OTHER }

    protected IncomeEntry() {}

    public UUID        getId()          { return id; }
    public String      getEntryNumber() { return entryNumber; }
    public LocalDate   getEntryDate()   { return entryDate; }
    public SourceType  getSourceType()  { return sourceType; }
    public UUID        getSourceId()    { return sourceId; }
    public String      getPatientName() { return patientName; }
    public BigDecimal  getAmount()      { return amount; }
    public String      getDescription() { return description; }
    public PaymentMode getPaymentMode() { return paymentMode; }
    public String      getReferenceNo() { return referenceNo; }
    public String      getReceivedBy()  { return receivedBy; }
    public String      getNotes()       { return notes; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final IncomeEntry e = new IncomeEntry();
        public Builder entryNumber(String v)     { e.entryNumber = v; return this; }
        public Builder entryDate(LocalDate v)    { e.entryDate   = v; return this; }
        public Builder sourceType(SourceType v)  { e.sourceType  = v; return this; }
        public Builder sourceId(UUID v)          { e.sourceId    = v; return this; }
        public Builder patientName(String v)     { e.patientName = v; return this; }
        public Builder amount(BigDecimal v)      { e.amount      = v; return this; }
        public Builder description(String v)     { e.description = v; return this; }
        public Builder paymentMode(PaymentMode v){ e.paymentMode = v; return this; }
        public Builder referenceNo(String v)     { e.referenceNo = v; return this; }
        public Builder receivedBy(String v)      { e.receivedBy  = v; return this; }
        public Builder notes(String v)           { e.notes       = v; return this; }
        public IncomeEntry build()               { return e; }
    }
}
