package com.smarthospital.modules.hr.repository;

import com.smarthospital.modules.hr.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DesignationRepository extends JpaRepository<Designation, UUID> {
    List<Designation> findByActiveTrue();
    List<Designation> findByDepartmentIdAndActiveTrue(UUID departmentId);
}
