package com.smarthospital.modules.hr.repository;

import com.smarthospital.modules.hr.domain.HrDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HrDepartmentRepository extends JpaRepository<HrDepartment, UUID> {
    List<HrDepartment> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
    boolean existsByCodeIgnoreCase(String code);
}
