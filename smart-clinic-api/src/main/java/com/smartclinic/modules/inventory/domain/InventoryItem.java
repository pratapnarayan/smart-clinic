package com.smartclinic.modules.inventory.domain;

import com.smartclinic.core.audit.AuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
    name = "inventory_items",
    indexes = {
        @Index(name = "idx_inv_items_category",  columnList = "category_id"),
        @Index(name = "idx_inv_items_low_stock", columnList = "current_stock, reorder_level")
    }
)
public class InventoryItem extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "item_code", nullable = false, unique = true, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    /** Snapshot so history survives category renames */
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(nullable = false, length = 30)
    private String unit;

    @Column(name = "reorder_level", nullable = false)
    private int reorderLevel = 10;

    @Column(name = "current_stock", nullable = false)
    private int currentStock = 0;

    protected InventoryItem() {}

    public UUID   getId()           { return id; }
    public String getItemCode()     { return itemCode; }
    public String getName()         { return name; }
    public String getDescription()  { return description; }
    public UUID   getCategoryId()   { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getUnit()         { return unit; }
    public int    getReorderLevel() { return reorderLevel; }
    public int    getCurrentStock() { return currentStock; }
    public boolean isLowStock()     { return currentStock <= reorderLevel; }

    public void setItemCode(String v)     { this.itemCode     = v; }
    public void setName(String v)         { this.name         = v; }
    public void setDescription(String v)  { this.description  = v; }
    public void setCategoryId(UUID v)     { this.categoryId   = v; }
    public void setCategoryName(String v) { this.categoryName = v; }
    public void setUnit(String v)         { this.unit         = v; }
    public void setReorderLevel(int v)    { this.reorderLevel = v; }
    public void setCurrentStock(int v)    { this.currentStock = v; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private final InventoryItem i = new InventoryItem();
        public Builder itemCode(String v)     { i.itemCode     = v; return this; }
        public Builder name(String v)         { i.name         = v; return this; }
        public Builder description(String v)  { i.description  = v; return this; }
        public Builder categoryId(UUID v)     { i.categoryId   = v; return this; }
        public Builder categoryName(String v) { i.categoryName = v; return this; }
        public Builder unit(String v)         { i.unit         = v; return this; }
        public Builder reorderLevel(int v)    { i.reorderLevel = v; return this; }
        public Builder currentStock(int v)    { i.currentStock = v; return this; }
        public InventoryItem build()          { return i; }
    }
}
