package com.smarthospital.modules.frontoffice.dto;

import com.smarthospital.modules.frontoffice.domain.Appointment.AppointmentStatus;
import com.smarthospital.modules.frontoffice.domain.Appointment.AppointmentType;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentUpdateRequest(
        UUID              doctorId,
        String            doctorName,
        String            department,
        LocalDate         appointmentDate,
        String            timeSlot,
        AppointmentType   appointmentType,
        AppointmentStatus status,
        String            notes
) {}
