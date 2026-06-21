package com.smartclinic.modules.ipd.dto;

import com.smartclinic.modules.ipd.domain.IpdAdmission;
import com.smartclinic.modules.ipd.domain.IpdAdmission.AdmissionStatus;
import com.smartclinic.modules.ipd.domain.IpdAdmission.DischargeCondition;
import com.smartclinic.modules.ipd.domain.IpdAdmission.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record IpdAdmissionResponse(
        UUID                    id,
        String                  admissionNumber,
        UUID                    patientId,
        String                  patientName,
        UUID                    opdVisitId,
        LocalDateTime           admissionDate,
        UUID                    wardId,
        UUID                    bedId,
        UUID                    doctorId,
        String                  doctorName,
        String                  admissionDiagnosis,
        String                  notes,
        AdmissionStatus         status,
        LocalDateTime           dischargeDate,
        String                  finalDiagnosis,
        DischargeCondition      conditionAtDischarge,
        String                  dischargeNotes,
        String                  followUpInstructions,
        BigDecimal              totalCharges,
        BigDecimal              discount,
        BigDecimal              netAmount,
        PaymentStatus           paymentStatus,
        List<IpdChargeResponse> charges,
        Instant                 createdAt
) {
    public static IpdAdmissionResponse from(IpdAdmission a) {
        return new IpdAdmissionResponse(
                a.getId(), a.getAdmissionNumber(),
                a.getPatientId(), a.getPatientName(),
                a.getOpdVisitId(), a.getAdmissionDate(),
                a.getWardId(), a.getBedId(),
                a.getDoctorId(), a.getDoctorName(),
                a.getAdmissionDiagnosis(), a.getNotes(),
                a.getStatus(),
                a.getDischargeDate(), a.getFinalDiagnosis(),
                a.getConditionAtDischarge(), a.getDischargeNotes(), a.getFollowUpInstructions(),
                a.getTotalCharges(), a.getDiscount(), a.getNetAmount(), a.getPaymentStatus(),
                a.getCharges().stream().map(IpdChargeResponse::from).toList(),
                a.getCreatedAt()
        );
    }
}
