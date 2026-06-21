package com.smartclinic.modules.opd.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
public class Prescription extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private OpdVisit visit;

    @Column(columnDefinition = "TEXT")
    private String advice;          // free-text advice to patient

    @Column(name = "follow_up_days")
    private Integer followUpDays;   // e.g. 7 → "review after 7 days"

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn(name = "sort_order")
    private List<PrescriptionItem> items = new ArrayList<>();

    protected Prescription() {}

    public Prescription(OpdVisit visit) {
        this.visit = visit;
    }

    public UUID              getId()          { return id; }
    public OpdVisit          getVisit()       { return visit; }
    public String            getAdvice()      { return advice; }
    public Integer           getFollowUpDays(){ return followUpDays; }
    public List<PrescriptionItem> getItems()  { return items; }

    public void setAdvice(String v)       { this.advice      = v; }
    public void setFollowUpDays(Integer v){ this.followUpDays = v; }

    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }
}
