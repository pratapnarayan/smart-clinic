package com.smartclinic.modules.doctor.dto;

public record DoctorDashboardResponse(
    long totalDoctors,
    long activeDoctors,
    long availableToday,
    long totalSpecializations
) {}
