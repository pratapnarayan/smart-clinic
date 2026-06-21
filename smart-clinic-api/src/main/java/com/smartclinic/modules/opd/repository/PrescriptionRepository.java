package com.smartclinic.modules.opd.repository;

import com.smartclinic.modules.opd.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByVisitId(UUID visitId);

    boolean existsByVisitId(UUID visitId);
}
