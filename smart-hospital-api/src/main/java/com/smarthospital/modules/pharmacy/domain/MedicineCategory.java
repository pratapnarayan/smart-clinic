package com.smarthospital.modules.pharmacy.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "medicine_categories")
@EntityListeners(AuditingEntityListener.class)
public class MedicineCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    protected MedicineCategory() {}

    public MedicineCategory(String name) { this.name = name; }

    public UUID    getId()        { return id; }
    public String  getName()      { return name; }
    public Instant getCreatedAt() { return createdAt; }

    public void setName(String v) { this.name = v; }
}
