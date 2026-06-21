package com.smartclinic.modules.doctor.dto;

import com.smartclinic.modules.doctor.domain.DoctorSchedule.DayOfWeek;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DoctorScheduleRequest(
    @NotNull DayOfWeek dayOfWeek,
    @NotNull LocalTime shiftStart,
    @NotNull LocalTime shiftEnd,
    int slotDurationMins
) {}
