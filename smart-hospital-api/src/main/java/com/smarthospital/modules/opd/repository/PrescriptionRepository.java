package com.smarthospital.modules.opd.repository;

import com.smarthospital.modules.opd.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByVisitId(UUID visitId);

    boolean existsByVisitId(UUID visitId);
}
