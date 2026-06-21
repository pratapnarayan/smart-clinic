package com.smartclinic.modules.opd.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    /** Medicine name stored as text — pharmacy module manages the actual catalogue. */
    @Column(name = "medicine_name", nullable = false, length = 200)
    private String medicineName;

    /** e.g. "1 tablet", "5 ml", "1 drop" */
    @Column(nullable = false, length = 50)
    private String dose;

    /** e.g. "Twice daily", "TDS", "Once at night" */
    @Column(nullable = false, length = 80)
    private String frequency;

    /** e.g. "5 days", "2 weeks", "Ongoing" */
    @Column(nullable = false, length = 50)
    private String duration;

    /** e.g. "After meals", "Before sleep", "With water" */
    @Column(length = 200)
    private String instructions;

    @Column(name = "sort_order")
    private int sortOrder;

    protected PrescriptionItem() {}

    public PrescriptionItem(String medicineName, String dose,
                            String frequency, String duration, String instructions) {
        this.medicineName = medicineName;
        this.dose         = dose;
        this.frequency    = frequency;
        this.duration     = duration;
        this.instructions = instructions;
    }

    public UUID         getId()            { return id; }
    public Prescription getPrescription()  { return prescription; }
    public String       getMedicineName()  { return medicineName; }
    public String       getDose()          { return dose; }
    public String       getFrequency()     { return frequency; }
    public String       getDuration()      { return duration; }
    public String       getInstructions()  { return instructions; }
    public int          getSortOrder()     { return sortOrder; }

    public void setPrescription(Prescription v) { this.prescription = v; }
    public void setMedicineName(String v)  { this.medicineName = v; }
    public void setDose(String v)          { this.dose         = v; }
    public void setFrequency(String v)     { this.frequency    = v; }
    public void setDuration(String v)      { this.duration     = v; }
    public void setInstructions(String v)  { this.instructions = v; }
    public void setSortOrder(int v)        { this.sortOrder    = v; }
}
