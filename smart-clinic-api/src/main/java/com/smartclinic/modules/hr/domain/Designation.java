package com.smartclinic.modules.hr.domain;

import com.smartclinic.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "designations",
       uniqueConstraints = @UniqueConstraint(name = "uq_desig_title_dept",
                                             columnNames = {"title", "department_id"}))
public class Designation extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(nullable = false)
    private boolean active = true;

    protected Designation() {}

    public UUID    getId()           { return id; }
    public String  getTitle()        { return title; }
    public UUID    getDepartmentId() { return departmentId; }
    public boolean isActive()        { return active; }

    public void setTitle(String v)        { this.title        = v; }
    public void setDepartmentId(UUID v)   { this.departmentId = v; }
    public void setActive(boolean v)      { this.active       = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final Designation d = new Designation();
        public Builder title(String v)        { d.title        = v; return this; }
        public Builder departmentId(UUID v)   { d.departmentId = v; return this; }
        public Builder active(boolean v)      { d.active       = v; return this; }
        public Designation build()            { return d; }
    }
}
