package com.smartclinic.modules.hr.domain;

import com.smartclinic.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "hr_departments")
public class HrDepartment extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false)
    private boolean active = true;

    protected HrDepartment() {}

    public UUID    getId()     { return id; }
    public String  getName()   { return name; }
    public String  getCode()   { return code; }
    public boolean isActive()  { return active; }

    public void setName(String v)    { this.name   = v; }
    public void setCode(String v)    { this.code   = v; }
    public void setActive(boolean v) { this.active = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final HrDepartment d = new HrDepartment();
        public Builder name(String v)    { d.name   = v; return this; }
        public Builder code(String v)    { d.code   = v; return this; }
        public Builder active(boolean v) { d.active = v; return this; }
        public HrDepartment build()      { return d; }
    }
}
