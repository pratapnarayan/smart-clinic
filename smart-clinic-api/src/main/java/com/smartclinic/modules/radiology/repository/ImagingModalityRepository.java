package com.smartclinic.modules.radiology.repository;

import com.smartclinic.modules.radiology.domain.ImagingModality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImagingModalityRepository extends JpaRepository<ImagingModality, UUID> {
    List<ImagingModality> findByActiveTrue();
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
}
