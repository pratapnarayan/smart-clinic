package com.smartclinic.modules.pathology.repository;

import com.smartclinic.modules.pathology.domain.LabTestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabTestCategoryRepository extends JpaRepository<LabTestCategory, UUID> {
    List<LabTestCategory> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
