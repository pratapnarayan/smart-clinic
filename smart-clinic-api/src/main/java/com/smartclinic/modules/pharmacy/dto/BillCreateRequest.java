package com.smartclinic.modules.pharmacy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BillCreateRequest(
        UUID   patientId,         // nullable — OTC sale
        @Size(max = 20) String paymentMode,
        @DecimalMin("0.00") BigDecimal discount,
        @NotEmpty @Valid List<BillItemRequest> items
) {}
