package com.smarthospital.modules.finance.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "expense_categories")
public class ExpenseCategory extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    protected ExpenseCategory() {}

    public UUID    getId()          { return id; }
    public String  getName()        { return name; }
    public String  getDescription() { return description; }
    public boolean isActive()       { return active; }

    public void setName(String v)        { this.name        = v; }
    public void setDescription(String v) { this.description = v; }
    public void setActive(boolean v)     { this.active      = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final ExpenseCategory c = new ExpenseCategory();
        public Builder name(String v)        { c.name        = v; return this; }
        public Builder description(String v) { c.description = v; return this; }
        public ExpenseCategory build()       { return c; }
    }
}
