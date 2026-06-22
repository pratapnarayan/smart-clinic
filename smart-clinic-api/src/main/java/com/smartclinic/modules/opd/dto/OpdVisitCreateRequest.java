package com.smartclinic.modules.opd.dto;

import com.smartclinic.modules.opd.domain.OpdVisit.VisitSource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OpdVisitCreateRequest(

        @NotNull UUID patientId,

        /** Visit date — defaults to today in service if null */
        LocalDate visitDate,

        @Size(max = 100) String department,

        UUID   doctorId,
        @Size(max = 200) String doctorName,

        @Size(max = 2000) String symptoms,

        @NotNull @DecimalMin("0.00")
        BigDecimal consultationFee,

        @Valid List<OpdChargeRequest> charges,

        /** Set by check-in flow — null for walk-ins */
        UUID appointmentId,

        /** Defaults to WALK_IN; set to APPOINTMENT by check-in endpoint */
        VisitSource visitSource

) {}
