package com.smarthospital.modules.frontoffice.dto;

import com.smarthospital.modules.frontoffice.domain.Appointment;
import com.smarthospital.modules.frontoffice.domain.Appointment.AppointmentStatus;
import com.smarthospital.modules.frontoffice.domain.Appointment.AppointmentType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AppointmentResponse(
        UUID              id,
        String            appointmentNumber,
        UUID              patientId,
        String            patientName,
        String            patientMobile,
        UUID              doctorId,
        String            doctorName,
        String            department,
        LocalDate         appointmentDate,
        String            timeSlot,
        AppointmentType   appointmentType,
        AppointmentStatus status,
        String            notes,
        Instant           createdAt
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(), a.getAppointmentNumber(),
                a.getPatientId(), a.getPatientName(), a.getPatientMobile(),
                a.getDoctorId(), a.getDoctorName(), a.getDepartment(),
                a.getAppointmentDate(), a.getTimeSlot(),
                a.getAppointmentType(), a.getStatus(), a.getNotes(),
                a.getCreatedAt()
        );
    }
}
