package com.smartclinic.modules.frontoffice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CheckInRequest(
        @Size(max = 2000) String symptoms,
        @DecimalMin("0.00") BigDecimal consultationFee
) {}
