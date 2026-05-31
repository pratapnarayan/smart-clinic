package com.smarthospital.modules.inventory.repository;

import com.smarthospital.modules.inventory.domain.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    Page<InventoryItem> findByCategoryId(UUID categoryId, Pageable pageable);

    @Query("SELECT i FROM InventoryItem i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<InventoryItem> search(@Param("q") String q, Pageable pageable);

    @Query("SELECT i FROM InventoryItem i WHERE i.currentStock <= i.reorderLevel ORDER BY i.currentStock ASC")
    List<InventoryItem> findLowStockItems();

    @Query("SELECT i FROM InventoryItem i WHERE i.currentStock <= i.reorderLevel ORDER BY i.currentStock ASC")
    Page<InventoryItem> findLowStockItemsPaged(Pageable pageable);

    boolean existsByItemCodeIgnoreCase(String itemCode);
    boolean existsByItemCodeIgnoreCaseAndIdNot(String itemCode, UUID id);
}
