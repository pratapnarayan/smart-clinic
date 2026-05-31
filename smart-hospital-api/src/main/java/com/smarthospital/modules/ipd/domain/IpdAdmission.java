package com.smarthospital.modules.ipd.domain;

import com.smarthospital.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "ipd_admissions",
    indexes = {
        @Index(name = "idx_ipd_patient_id",     columnList = "patient_id"),
        @Index(name = "idx_ipd_admission_date", columnList = "admission_date"),
        @Index(name = "idx_ipd_bed_id",         columnList = "bed_id")
    }
)
public class IpdAdmission extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "admission_number", nullable = false, unique = true, length = 30)
    private String admissionNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "opd_visit_id")
    private UUID opdVisitId;

    @Column(name = "admission_date", nullable = false)
    private LocalDateTime admissionDate;

    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    @Column(name = "bed_id", nullable = false)
    private UUID bedId;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    @Column(name = "admission_diagnosis", columnDefinition = "TEXT")
    private String admissionDiagnosis;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdmissionStatus status = AdmissionStatus.ADMITTED;

    // ── Discharge (null until discharged) ────────────────────────────────────

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Column(name = "final_diagnosis", columnDefinition = "TEXT")
    private String finalDiagnosis;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_at_discharge", length = 20)
    private DischargeCondition conditionAtDischarge;

    @Column(name = "discharge_notes", columnDefinition = "TEXT")
    private String dischargeNotes;

    @Column(name = "follow_up_instructions", columnDefinition = "TEXT")
    private String followUpInstructions;

    // ── Billing ───────────────────────────────────────────────────────────────

    @Column(name = "total_charges", precision = 10, scale = 2)
    private BigDecimal totalCharges = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @OneToMany(mappedBy = "admission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpdCharge> charges = new ArrayList<>();

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum AdmissionStatus  { ADMITTED, TRANSFERRED, DISCHARGED, DECEASED }
    public enum DischargeCondition { STABLE, IMPROVED, CRITICAL, UNCHANGED, DECEASED }
    public enum PaymentStatus    { PENDING, PAID, PARTIAL, WAIVED }

    protected IpdAdmission() {}

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID             getId()                   { return id; }
    public String           getAdmissionNumber()      { return admissionNumber; }
    public UUID             getPatientId()            { return patientId; }
    public String           getPatientName()          { return patientName; }
    public UUID             getOpdVisitId()           { return opdVisitId; }
    public LocalDateTime    getAdmissionDate()        { return admissionDate; }
    public UUID             getWardId()               { return wardId; }
    public UUID             getBedId()                { return bedId; }
    public UUID             getDoctorId()             { return doctorId; }
    public String           getDoctorName()           { return doctorName; }
    public String           getAdmissionDiagnosis()   { return admissionDiagnosis; }
    public String           getNotes()                { return notes; }
    public AdmissionStatus  getStatus()               { return status; }
    public LocalDateTime    getDischargeDate()        { return dischargeDate; }
    public String           getFinalDiagnosis()       { return finalDiagnosis; }
    public DischargeCondition getConditionAtDischarge(){ return conditionAtDischarge; }
    public String           getDischargeNotes()       { return dischargeNotes; }
    public String           getFollowUpInstructions() { return followUpInstructions; }
    public BigDecimal       getTotalCharges()         { return totalCharges; }
    public BigDecimal       getDiscount()             { return discount; }
    public BigDecimal       getNetAmount()            { return netAmount; }
    public PaymentStatus    getPaymentStatus()        { return paymentStatus; }
    public List<IpdCharge>  getCharges()              { return charges; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setWardId(UUID v)                        { this.wardId               = v; }
    public void setBedId(UUID v)                         { this.bedId                = v; }
    public void setDoctorId(UUID v)                      { this.doctorId             = v; }
    public void setDoctorName(String v)                  { this.doctorName           = v; }
    public void setAdmissionDiagnosis(String v)          { this.admissionDiagnosis   = v; }
    public void setNotes(String v)                       { this.notes                = v; }
    public void setStatus(AdmissionStatus v)             { this.status               = v; }
    public void setDischargeDate(LocalDateTime v)        { this.dischargeDate        = v; }
    public void setFinalDiagnosis(String v)              { this.finalDiagnosis       = v; }
    public void setConditionAtDischarge(DischargeCondition v){ this.conditionAtDischarge = v; }
    public void setDischargeNotes(String v)              { this.dischargeNotes       = v; }
    public void setFollowUpInstructions(String v)        { this.followUpInstructions = v; }
    public void setDiscount(BigDecimal v)                { this.discount             = v; }
    public void setPaymentStatus(PaymentStatus v)        { this.paymentStatus        = v; }

    public void recalculateTotals() {
        this.totalCharges = charges.stream()
                .map(IpdCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.netAmount = totalCharges.subtract(discount);
    }

    // ── Builder ───────────────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final IpdAdmission a = new IpdAdmission();
        public Builder admissionNumber(String v)     { a.admissionNumber    = v; return this; }
        public Builder patientId(UUID v)             { a.patientId          = v; return this; }
        public Builder patientName(String v)         { a.patientName        = v; return this; }
        public Builder opdVisitId(UUID v)            { a.opdVisitId         = v; return this; }
        public Builder admissionDate(LocalDateTime v){ a.admissionDate      = v; return this; }
        public Builder wardId(UUID v)                { a.wardId             = v; return this; }
        public Builder bedId(UUID v)                 { a.bedId              = v; return this; }
        public Builder doctorId(UUID v)              { a.doctorId           = v; return this; }
        public Builder doctorName(String v)          { a.doctorName         = v; return this; }
        public Builder admissionDiagnosis(String v)  { a.admissionDiagnosis = v; return this; }
        public IpdAdmission build()                  { return a; }
    }
}
