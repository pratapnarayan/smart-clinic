package com.smartclinic.modules.radiology.repository;

import com.smartclinic.modules.radiology.domain.RadiologyOrder;
import com.smartclinic.modules.radiology.domain.RadiologyOrder.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RadiologyOrderRepository extends JpaRepository<RadiologyOrder, UUID> {

    Page<RadiologyOrder> findByPatientId(UUID patientId, Pageable pageable);
    Page<RadiologyOrder> findByStatus(OrderStatus status, Pageable pageable);
    long countByStatus(OrderStatus status);

    @Query(value = "SELECT COUNT(*) + 1 FROM radiology_orders WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
