package com.smarthospital.modules.bloodbank.dto;

import com.smarthospital.modules.bloodbank.domain.BloodDonor;
import com.smarthospital.modules.bloodbank.domain.BloodDonor.Gender;
import com.smarthospital.modules.bloodbank.domain.BloodGroup;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BloodDonorResponse(
        UUID      id,
        String    donorNumber,
        String    firstName,
        String    lastName,
        Gender    gender,
        LocalDate dateOfBirth,
        BloodGroup bloodGroup,
        String    bloodGroupDisplay,
        String    mobile,
        String    email,
        LocalDate lastDonationDate,
        int       totalDonations,
        boolean   active,
        Instant   createdAt
) {
    public static BloodDonorResponse from(BloodDonor d) {
        return new BloodDonorResponse(
                d.getId(), d.getDonorNumber(),
                d.getFirstName(), d.getLastName(),
                d.getGender(), d.getDateOfBirth(),
                d.getBloodGroup(), d.getBloodGroup().display(),
                d.getMobile(), d.getEmail(),
                d.getLastDonationDate(), d.getTotalDonations(),
                d.isActive(), d.getCreatedAt()
        );
    }
}
