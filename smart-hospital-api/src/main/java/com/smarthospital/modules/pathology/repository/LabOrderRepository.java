package com.smarthospital.modules.pathology.repository;

import com.smarthospital.modules.pathology.domain.LabOrder;
import com.smarthospital.modules.pathology.domain.LabOrder.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LabOrderRepository extends JpaRepository<LabOrder, UUID> {

    Page<LabOrder> findByPatientId(UUID patientId, Pageable pageable);

    Page<LabOrder> findByStatus(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query(value = "SELECT COUNT(*) + 1 FROM lab_orders WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
