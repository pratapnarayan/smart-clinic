package com.smarthospital.modules.opd.dto;

import com.smarthospital.modules.opd.domain.OpdVisit.PaymentStatus;
import com.smarthospital.modules.opd.domain.OpdVisit.VisitStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Used for the PATCH /opd/visits/{id} endpoint — all fields optional. */
public record OpdVisitUpdateRequest(
        @Size(max = 100)  String department,
        @Size(max = 200)  String doctorName,
        @Size(max = 2000) String symptoms,
        @Size(max = 2000) String diagnosis,
        @Size(max = 2000) String notes,
        @DecimalMin("0.00") BigDecimal discount,
        PaymentStatus paymentStatus,
        VisitStatus   visitStatus
) {}
