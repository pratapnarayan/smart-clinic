package com.smarthospital.modules.radiology.repository;

import com.smarthospital.modules.radiology.domain.ImagingStudy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImagingStudyRepository extends JpaRepository<ImagingStudy, UUID> {
    List<ImagingStudy> findByModalityIdAndActiveTrue(UUID modalityId);
    List<ImagingStudy> findByActiveTrue();
    boolean existsByCodeIgnoreCase(String code);
}
