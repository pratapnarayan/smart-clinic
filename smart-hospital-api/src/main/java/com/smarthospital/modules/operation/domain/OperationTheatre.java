package com.smarthospital.modules.operation.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ot_theatres")
public class OperationTheatre extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "theatre_number", nullable = false, unique = true, length = 20)
    private String theatreNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TheatreType type = TheatreType.GENERAL;

    @Column(nullable = false)
    private boolean active = true;

    public enum TheatreType {
        GENERAL, CARDIAC, NEURO, ORTHO, PAEDIATRIC, EMERGENCY, DIAGNOSTIC
    }

    protected OperationTheatre() {}

    public UUID        getId()            { return id; }
    public String      getTheatreNumber() { return theatreNumber; }
    public String      getName()          { return name; }
    public TheatreType getType()          { return type; }
    public boolean     isActive()         { return active; }

    public void setName(String v)        { this.name   = v; }
    public void setType(TheatreType v)   { this.type   = v; }
    public void setActive(boolean v)     { this.active = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final OperationTheatre t = new OperationTheatre();
        public Builder theatreNumber(String v)  { t.theatreNumber = v; return this; }
        public Builder name(String v)           { t.name          = v; return this; }
        public Builder type(TheatreType v)      { t.type          = v; return this; }
        public OperationTheatre build()         { return t; }
    }
}
