package com.smartclinic.modules.frontoffice.dto;

import com.smartclinic.modules.frontoffice.domain.Appointment.AppointmentStatus;
import com.smartclinic.modules.frontoffice.domain.Appointment.AppointmentType;

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
