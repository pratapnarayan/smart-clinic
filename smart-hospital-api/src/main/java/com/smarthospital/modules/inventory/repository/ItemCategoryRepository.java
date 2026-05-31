package com.smarthospital.modules.inventory.repository;

import com.smarthospital.modules.inventory.domain.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, UUID> {
    List<ItemCategory> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
