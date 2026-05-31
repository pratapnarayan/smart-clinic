package com.smarthospital.modules.pathology.repository;

import com.smarthospital.modules.pathology.domain.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabTestRepository extends JpaRepository<LabTest, UUID> {
    List<LabTest> findByCategoryIdAndActiveTrue(UUID categoryId);
    List<LabTest> findByActiveTrue();
    boolean existsByCodeIgnoreCase(String code);
}
