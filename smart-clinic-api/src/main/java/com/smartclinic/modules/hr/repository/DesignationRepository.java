package com.smartclinic.modules.hr.repository;

import com.smartclinic.modules.hr.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DesignationRepository extends JpaRepository<Designation, UUID> {
    List<Designation> findByActiveTrue();
    List<Designation> findByDepartmentIdAndActiveTrue(UUID departmentId);
    Optional<Designation> findByTitleAndDepartmentId(String title, UUID departmentId);
}
