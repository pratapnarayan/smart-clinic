package com.smarthospital.modules.ipd.dto;

import com.smarthospital.modules.ipd.domain.IpdAdmission.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record IpdAdmissionUpdateRequest(
        UUID          doctorId,
        String        doctorName,
        String        admissionDiagnosis,
        String        notes,
        UUID          wardId,
        UUID          bedId,
        BigDecimal    discount,
        PaymentStatus paymentStatus
) {}
