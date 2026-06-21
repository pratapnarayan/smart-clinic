package com.smartclinic.modules.auth.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uq_users_email", columnNames = "email")
)
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * The tenant schema this user belongs to (e.g. "hospital_001").
     * Stored on the entity so it can be embedded in the JWT without a DB call on every request.
     */
    @Column(name = "tenant_id", nullable = false, length = 63)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    protected User() {}

    // Getters
    public UUID    getId()           { return id; }
    public String  getEmail()        { return email; }
    public String  getPasswordHash() { return passwordHash; }
    public String  getFirstName()    { return firstName; }
    public String  getLastName()     { return lastName; }
    public String  getTenantId()     { return tenantId; }
    public Role    getRole()         { return role; }
    public boolean isActive()        { return active; }

    // Setters
    public void setEmail(String v)        { this.email        = v; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public void setFirstName(String v)    { this.firstName    = v; }
    public void setLastName(String v)     { this.lastName     = v; }
    public void setTenantId(String v)     { this.tenantId     = v; }
    public void setRole(Role v)           { this.role         = v; }
    public void setActive(boolean v)      { this.active       = v; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final User u = new User();
        public Builder email(String v)        { u.email        = v; return this; }
        public Builder passwordHash(String v) { u.passwordHash = v; return this; }
        public Builder firstName(String v)    { u.firstName    = v; return this; }
        public Builder lastName(String v)     { u.lastName     = v; return this; }
        public Builder tenantId(String v)     { u.tenantId     = v; return this; }
        public Builder role(Role v)           { u.role         = v; return this; }
        public Builder active(boolean v)      { u.active       = v; return this; }
        public User build()                   { return u; }
    }
}
