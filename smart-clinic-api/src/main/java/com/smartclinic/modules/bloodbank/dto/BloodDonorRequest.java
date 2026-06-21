package com.smartclinic.modules.bloodbank.dto;

import com.smartclinic.modules.bloodbank.domain.BloodDonor.Gender;
import com.smartclinic.modules.bloodbank.domain.BloodGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BloodDonorRequest(
        @NotBlank String    firstName,
        @NotBlank String    lastName,
        @NotNull  Gender    gender,
        @NotNull  LocalDate dateOfBirth,
        @NotNull  BloodGroup bloodGroup,
        String    mobile,
        String    email,
        String    address
) {}
