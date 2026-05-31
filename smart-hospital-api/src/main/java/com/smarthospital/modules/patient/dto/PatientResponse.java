package com.smarthospital.modules.patient.dto;

import com.smarthospital.modules.patient.domain.Patient;
import com.smarthospital.modules.patient.domain.Patient.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String mobile,
        String email,
        String address,
        String bloodGroup,
        String guardianName,
        String photoUrl,
        Instant createdAt
) {
    public static PatientResponse from(Patient p) {
        return new PatientResponse(
                p.getId(), p.getFirstName(), p.getLastName(),
                p.getDateOfBirth(), p.getGender(), p.getMobile(),
                p.getEmail(), p.getAddress(), p.getBloodGroup(),
                p.getGuardianName(), p.getPhotoUrl(), p.getCreatedAt()
        );
    }
}
