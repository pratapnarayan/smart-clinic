package com.smarthospital.modules.pharmacy.repository;

import com.smarthospital.modules.pharmacy.domain.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, UUID> {
    Optional<MedicineCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
