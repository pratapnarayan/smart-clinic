package com.smarthospital.modules.hr.repository;

import com.smarthospital.modules.hr.domain.LeaveRequest;
import com.smarthospital.modules.hr.domain.LeaveRequest.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    Page<LeaveRequest> findByEmployeeId(UUID employeeId, Pageable pageable);

    Page<LeaveRequest> findByStatus(LeaveStatus status, Pageable pageable);

    long countByStatus(LeaveStatus status);

    @Query("SELECT COUNT(l) FROM LeaveRequest l WHERE l.status = 'APPROVED' " +
           "AND l.fromDate <= :date AND l.toDate >= :date")
    long countOnLeaveOnDate(@Param("date") LocalDate date);

    @Query(value = "SELECT COUNT(*) + 1 FROM leave_requests WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);

    List<LeaveRequest> findByEmployeeIdAndStatus(UUID employeeId, LeaveStatus status);
}
