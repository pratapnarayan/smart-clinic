package com.smarthospital.modules.opd.dto;

import com.smarthospital.modules.opd.domain.OpdVisit;
import com.smarthospital.modules.opd.domain.OpdVisit.PaymentStatus;
import com.smarthospital.modules.opd.domain.OpdVisit.VisitStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OpdVisitResponse(
        UUID                    id,
        String                  visitNumber,
        UUID                    patientId,
        String                  patientName,
        LocalDate               visitDate,
        String                  department,
        UUID                    doctorId,
        String                  doctorName,
        String                  symptoms,
        String                  diagnosis,
        String                  notes,
        BigDecimal              consultationFee,
        BigDecimal              totalCharges,
        BigDecimal              discount,
        BigDecimal              netAmount,
        PaymentStatus           paymentStatus,
        VisitStatus             visitStatus,
        List<OpdChargeResponse> charges,
        PrescriptionResponse    prescription,
        Instant                 createdAt
) {
    public static OpdVisitResponse from(OpdVisit v) {
        return new OpdVisitResponse(
                v.getId(), v.getVisitNumber(),
                v.getPatientId(), v.getPatientName(),
                v.getVisitDate(), v.getDepartment(),
                v.getDoctorId(), v.getDoctorName(),
                v.getSymptoms(), v.getDiagnosis(), v.getNotes(),
                v.getConsultationFee(), v.getTotalCharges(),
                v.getDiscount(), v.getNetAmount(),
                v.getPaymentStatus(), v.getVisitStatus(),
                v.getCharges().stream().map(OpdChargeResponse::from).toList(),
                v.getPrescription() != null ? PrescriptionResponse.from(v.getPrescription()) : null,
                v.getCreatedAt()
        );
    }
}
