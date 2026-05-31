package com.smarthospital.modules.frontoffice.dto;

import com.smarthospital.modules.frontoffice.domain.Appointment.AppointmentType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentCreateRequest(
        @NotNull UUID            patientId,
        UUID                     doctorId,
        String                   doctorName,
        String                   department,
        @NotNull LocalDate       appointmentDate,
        String                   timeSlot,
        AppointmentType          appointmentType,
        String                   notes
) {}
