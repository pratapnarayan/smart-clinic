package com.smartclinic.modules.hr.repository;

import com.smartclinic.modules.hr.domain.AttendanceRecord;
import com.smartclinic.modules.hr.domain.AttendanceRecord.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {

    List<AttendanceRecord> findByAttendanceDateOrderByCreatedAtAsc(LocalDate date);

    List<AttendanceRecord> findByEmployeeIdOrderByAttendanceDateDesc(UUID employeeId);

    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate date);

    long countByAttendanceDateAndStatus(LocalDate date, AttendanceStatus status);
}
