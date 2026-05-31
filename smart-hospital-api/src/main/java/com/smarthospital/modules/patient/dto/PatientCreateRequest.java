package com.smarthospital.modules.patient.dto;

import com.smarthospital.modules.patient.domain.Patient.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientCreateRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotNull Gender gender,
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Must be 10-15 digits") String mobile,
        @Email @Size(max = 150) String email,
        String address,
        String bloodGroup,
        String guardianName,
        String guardianMobile
) {}
