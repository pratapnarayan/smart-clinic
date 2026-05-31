package com.smarthospital.modules.frontoffice.dto;

import com.smarthospital.modules.frontoffice.domain.OpdToken;
import com.smarthospital.modules.frontoffice.domain.OpdToken.TokenPriority;
import com.smarthospital.modules.frontoffice.domain.OpdToken.TokenStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OpdTokenResponse(
        UUID          id,
        String        tokenNumber,
        UUID          patientId,
        String        patientName,
        String        patientMobile,
        String        department,
        UUID          doctorId,
        String        doctorName,
        LocalDate     tokenDate,
        TokenPriority priority,
        TokenStatus   status,
        UUID          linkedAppointmentId,
        Instant       createdAt
) {
    public static OpdTokenResponse from(OpdToken t) {
        return new OpdTokenResponse(
                t.getId(), t.getTokenNumber(),
                t.getPatientId(), t.getPatientName(), t.getPatientMobile(),
                t.getDepartment(), t.getDoctorId(), t.getDoctorName(),
                t.getTokenDate(), t.getPriority(), t.getStatus(),
                t.getLinkedAppointmentId(), t.getCreatedAt()
        );
    }
}
