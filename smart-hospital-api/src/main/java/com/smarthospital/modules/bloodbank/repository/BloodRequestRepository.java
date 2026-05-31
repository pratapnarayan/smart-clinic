package com.smarthospital.modules.bloodbank.repository;

import com.smarthospital.modules.bloodbank.domain.BloodRequest;
import com.smarthospital.modules.bloodbank.domain.BloodRequest.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, UUID> {

    Page<BloodRequest> findByStatus(RequestStatus status, Pageable pageable);

    Page<BloodRequest> findByPatientId(UUID patientId, Pageable pageable);

    long countByStatusIn(List<RequestStatus> statuses);

    @Query(value = "SELECT COUNT(*) FROM blood_requests WHERE request_date = CURRENT_DATE",
           nativeQuery = true)
    long countToday();

    @Query(value = "SELECT COUNT(*) + 1 FROM blood_requests WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
