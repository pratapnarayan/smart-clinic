package com.smartclinic.modules.inventory.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "stock_issues",
    indexes = {
        @Index(name = "idx_issues_item_id", columnList = "item_id"),
        @Index(name = "idx_issues_date",    columnList = "issue_date"),
        @Index(name = "idx_issues_dept",    columnList = "issued_to")
    }
)
public class StockIssue extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "issue_number", nullable = false, unique = true, length = 30)
    private String issueNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "item_unit", nullable = false, length = 30)
    private String itemUnit;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "issued_to", nullable = false, length = 200)
    private String issuedTo;

    @Column(name = "issued_by", length = 200)
    private String issuedBy;

    @Column(length = 500)
    private String purpose;

    @Column(columnDefinition = "TEXT")
    private String notes;

    protected StockIssue() {}

    public UUID     getId()          { return id; }
    public String   getIssueNumber() { return issueNumber; }
    public LocalDate getIssueDate()  { return issueDate; }
    public UUID     getItemId()      { return itemId; }
    public String   getItemName()    { return itemName; }
    public String   getItemUnit()    { return itemUnit; }
    public int      getQuantity()    { return quantity; }
    public String   getIssuedTo()    { return issuedTo; }
    public String   getIssuedBy()    { return issuedBy; }
    public String   getPurpose()     { return purpose; }
    public String   getNotes()       { return notes; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final StockIssue i = new StockIssue();
        public Builder issueNumber(String v)  { i.issueNumber = v; return this; }
        public Builder issueDate(LocalDate v) { i.issueDate   = v; return this; }
        public Builder itemId(UUID v)         { i.itemId      = v; return this; }
        public Builder itemName(String v)     { i.itemName    = v; return this; }
        public Builder itemUnit(String v)     { i.itemUnit    = v; return this; }
        public Builder quantity(int v)        { i.quantity    = v; return this; }
        public Builder issuedTo(String v)     { i.issuedTo    = v; return this; }
        public Builder issuedBy(String v)     { i.issuedBy    = v; return this; }
        public Builder purpose(String v)      { i.purpose     = v; return this; }
        public Builder notes(String v)        { i.notes       = v; return this; }
        public StockIssue build()             { return i; }
    }
}
