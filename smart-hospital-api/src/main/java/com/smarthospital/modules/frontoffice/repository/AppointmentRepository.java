package com.smarthospital.modules.frontoffice.repository;

import com.smarthospital.modules.frontoffice.domain.Appointment;
import com.smarthospital.modules.frontoffice.domain.Appointment.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByAppointmentDate(LocalDate date, Pageable pageable);

    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);

    Page<Appointment> findByDoctorIdAndAppointmentDate(UUID doctorId, LocalDate date, Pageable pageable);

    List<Appointment> findByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    @Query(value = "SELECT COUNT(*) + 1 FROM appointments WHERE EXTRACT(YEAR FROM appointment_date) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
