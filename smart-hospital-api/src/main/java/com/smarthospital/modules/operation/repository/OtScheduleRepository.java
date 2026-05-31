package com.smarthospital.modules.operation.repository;

import com.smarthospital.modules.operation.domain.OtSchedule;
import com.smarthospital.modules.operation.domain.OtSchedule.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OtScheduleRepository extends JpaRepository<OtSchedule, UUID> {

    List<OtSchedule> findByScheduledDateOrderByScheduledStartAsc(LocalDate date);

    Page<OtSchedule> findByScheduledDate(LocalDate date, Pageable pageable);

    Page<OtSchedule> findByStatus(Status status, Pageable pageable);

    Page<OtSchedule> findByScheduledDateAndStatus(LocalDate date, Status status, Pageable pageable);

    Page<OtSchedule> findByTheatreIdAndScheduledDate(UUID theatreId, LocalDate date, Pageable pageable);

    long countByStatusAndScheduledDate(Status status, LocalDate date);

    long countByScheduledDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT s.theatreName, COUNT(s) FROM OtSchedule s " +
           "WHERE s.scheduledDate BETWEEN :from AND :to " +
           "AND s.status NOT IN ('CANCELLED') " +
           "GROUP BY s.theatreName ORDER BY COUNT(s) DESC")
    List<Object[]> countByTheatreForPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query(value = "SELECT COUNT(*) + 1 FROM ot_schedules WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
