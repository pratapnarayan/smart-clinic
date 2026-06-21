package com.smartclinic.modules.ipd.domain;

import com.smartclinic.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ipd_charges")
public class IpdCharge extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admission_id", nullable = false)
    private IpdAdmission admission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChargeCategory category = ChargeCategory.OTHER;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "charge_date", nullable = false)
    private LocalDate chargeDate = LocalDate.now();

    public enum ChargeCategory {
        BED_CHARGE, NURSING, DOCTOR_VISIT, PROCEDURE, MEDICINE, OTHER
    }

    protected IpdCharge() {}

    public IpdCharge(IpdAdmission admission, ChargeCategory category,
                     String description, BigDecimal amount, LocalDate chargeDate) {
        this.admission  = admission;
        this.category   = category;
        this.description = description;
        this.amount     = amount;
        this.chargeDate = chargeDate;
    }

    public UUID           getId()          { return id; }
    public IpdAdmission   getAdmission()   { return admission; }
    public ChargeCategory getCategory()    { return category; }
    public String         getDescription() { return description; }
    public BigDecimal     getAmount()      { return amount; }
    public LocalDate      getChargeDate()  { return chargeDate; }
}
