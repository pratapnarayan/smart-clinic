package com.smarthospital.modules.operation.domain;

import com.smarthospital.core.audit.CreatedOnlyAuditEntity;
import com.smarthospital.modules.inventory.domain.InventoryItem;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ot_consumables",
       indexes = @Index(name = "idx_ot_cons_schedule", columnList = "schedule_id"))
public class OtConsumable extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private OtSchedule schedule;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    /** Snapshot — survives item catalog edits */
    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "item_unit", nullable = false, length = 30)
    private String itemUnit;

    @Column(name = "quantity_used", nullable = false)
    private int quantityUsed;

    protected OtConsumable() {}

    public OtConsumable(OtSchedule schedule, InventoryItem item, int quantityUsed) {
        this.schedule     = schedule;
        this.itemId       = item.getId();
        this.itemName     = item.getName();
        this.itemUnit     = item.getUnit();
        this.quantityUsed = quantityUsed;
    }

    public UUID       getId()           { return id; }
    public OtSchedule getSchedule()     { return schedule; }
    public UUID       getItemId()       { return itemId; }
    public String     getItemName()     { return itemName; }
    public String     getItemUnit()     { return itemUnit; }
    public int        getQuantityUsed() { return quantityUsed; }
}
