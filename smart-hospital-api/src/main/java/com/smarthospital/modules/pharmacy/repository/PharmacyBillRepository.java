package com.smarthospital.modules.pharmacy.repository;

import com.smarthospital.modules.pharmacy.domain.PharmacyBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PharmacyBillRepository extends JpaRepository<PharmacyBill, UUID> {

    Optional<PharmacyBill> findByBillNumber(String billNumber);

    Page<PharmacyBill> findByPatientId(UUID patientId, Pageable pageable);

    Page<PharmacyBill> findByCreatedAtBetween(Instant from, Instant to, Pageable pageable);

    /** Bill number sequence: PH-YYYYMMDD-NNN */
    @Query(value = "SELECT COUNT(*) + 1 FROM pharmacy_bills " +
                   "WHERE DATE(created_at) = CURRENT_DATE",
           nativeQuery = true)
    long nextDailySequence();
}
