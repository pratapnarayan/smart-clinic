package com.smarthospital.modules.opd.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * One outpatient visit record.
 *
 * Cross-module rule (from arch doc Section 4.1):
 *   patientId is a plain UUID — we never JOIN across module boundaries.
 *   Fetch patient details via PatientService when needed.
 */
@Entity
@Table(
    name = "opd_visits",
    indexes = {
        @Index(name = "idx_opd_patient_id",   columnList = "patient_id"),
        @Index(name = "idx_opd_visit_date",   columnList = "visit_date"),
        @Index(name = "idx_opd_visit_number", columnList = "visit_number")
    }
)
public class OpdVisit extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Human-readable visit number, e.g. OPD-2026-00042 */
    @Column(name = "visit_number", nullable = false, unique = true, length = 30)
    private String visitNumber;

    // ── Patient reference (UUID only — no FK to patient table across modules) ──
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    /** Denormalised snapshot so visit history survives patient edits */
    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    // ── Clinical ──
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(length = 100)
    private String department;

    /** Doctor's userId (UUID) — plain reference, no FK across modules */
    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Billing ──
    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private BigDecimal consultationFee = BigDecimal.ZERO;

    @Column(name = "total_charges", precision = 10, scale = 2)
    private BigDecimal totalCharges = BigDecimal.ZERO;

    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", nullable = false, length = 20)
    private VisitStatus visitStatus = VisitStatus.REGISTERED;

    // ── Child collections ──
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpdCharge> charges = new ArrayList<>();

    @OneToOne(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Prescription prescription;

    // ── Enums ──
    public enum PaymentStatus { PENDING, PAID, PARTIAL, WAIVED }
    public enum VisitStatus   { REGISTERED, IN_PROGRESS, COMPLETED, CANCELLED }

    protected OpdVisit() {}

    // Getters
    public UUID          getId()               { return id; }
    public String        getVisitNumber()      { return visitNumber; }
    public UUID          getPatientId()        { return patientId; }
    public String        getPatientName()      { return patientName; }
    public LocalDate     getVisitDate()        { return visitDate; }
    public String        getDepartment()       { return department; }
    public UUID          getDoctorId()         { return doctorId; }
    public String        getDoctorName()       { return doctorName; }
    public String        getSymptoms()         { return symptoms; }
    public String        getDiagnosis()        { return diagnosis; }
    public String        getNotes()            { return notes; }
    public BigDecimal    getConsultationFee()  { return consultationFee; }
    public BigDecimal    getTotalCharges()     { return totalCharges; }
    public BigDecimal    getDiscount()         { return discount; }
    public BigDecimal    getNetAmount()        { return netAmount; }
    public PaymentStatus getPaymentStatus()    { return paymentStatus; }
    public VisitStatus   getVisitStatus()      { return visitStatus; }
    public List<OpdCharge>  getCharges()       { return charges; }
    public Prescription  getPrescription()     { return prescription; }

    // Setters
    public void setVisitNumber(String v)      { this.visitNumber     = v; }
    public void setPatientId(UUID v)          { this.patientId       = v; }
    public void setPatientName(String v)      { this.patientName     = v; }
    public void setVisitDate(LocalDate v)     { this.visitDate       = v; }
    public void setDepartment(String v)       { this.department      = v; }
    public void setDoctorId(UUID v)           { this.doctorId        = v; }
    public void setDoctorName(String v)       { this.doctorName      = v; }
    public void setSymptoms(String v)         { this.symptoms        = v; }
    public void setDiagnosis(String v)        { this.diagnosis       = v; }
    public void setNotes(String v)            { this.notes           = v; }
    public void setConsultationFee(BigDecimal v) { this.consultationFee = v; }
    public void setDiscount(BigDecimal v)     { this.discount        = v; }
    public void setPaymentStatus(PaymentStatus v) { this.paymentStatus = v; }
    public void setVisitStatus(VisitStatus v) { this.visitStatus     = v; }
    public void setPrescription(Prescription v)  { this.prescription = v; }

    /** Recalculates totalCharges and netAmount from charges list + consultation fee. */
    public void recalculateTotals() {
        BigDecimal chargeSum = charges.stream()
                .map(OpdCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalCharges = consultationFee.add(chargeSum);
        this.netAmount    = totalCharges.subtract(discount);
    }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final OpdVisit v = new OpdVisit();
        public Builder visitNumber(String x)     { v.visitNumber    = x; return this; }
        public Builder patientId(UUID x)         { v.patientId      = x; return this; }
        public Builder patientName(String x)     { v.patientName    = x; return this; }
        public Builder visitDate(LocalDate x)    { v.visitDate      = x; return this; }
        public Builder department(String x)      { v.department     = x; return this; }
        public Builder doctorId(UUID x)          { v.doctorId       = x; return this; }
        public Builder doctorName(String x)      { v.doctorName     = x; return this; }
        public Builder symptoms(String x)        { v.symptoms       = x; return this; }
        public Builder consultationFee(BigDecimal x){ v.consultationFee = x; return this; }
        public OpdVisit build()                  { return v; }
    }
}
