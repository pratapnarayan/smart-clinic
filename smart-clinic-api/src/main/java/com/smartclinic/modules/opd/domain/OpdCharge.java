package com.smartclinic.modules.opd.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/** Itemised charge line on an OPD visit (e.g. ECG, dressing, injection fee). */
@Entity
@Table(name = "opd_charges")
public class OpdCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private OpdVisit visit;

    @Column(nullable = false, length = 150)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 50)
    private String category;    // e.g. PROCEDURE, LAB, NURSING

    protected OpdCharge() {}

    public OpdCharge(OpdVisit visit, String description, BigDecimal amount, String category) {
        this.visit       = visit;
        this.description = description;
        this.amount      = amount;
        this.category    = category;
    }

    public UUID       getId()          { return id; }
    public OpdVisit   getVisit()       { return visit; }
    public String     getDescription() { return description; }
    public BigDecimal getAmount()      { return amount; }
    public String     getCategory()    { return category; }

    public void setDescription(String v)  { this.description = v; }
    public void setAmount(BigDecimal v)   { this.amount      = v; }
    public void setCategory(String v)     { this.category    = v; }
}
