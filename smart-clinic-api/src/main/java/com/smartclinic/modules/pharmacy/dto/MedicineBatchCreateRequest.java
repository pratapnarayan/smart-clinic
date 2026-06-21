package com.smartclinic.modules.pharmacy.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MedicineBatchCreateRequest(
        @NotNull  UUID       medicineId,
        @NotBlank @Size(max = 50) String batchNumber,
        @NotNull  @Future   LocalDate expiryDate,
        @Min(1)             int quantity,
        @NotNull @DecimalMin("0.01") BigDecimal purchasePrice,
        @NotNull @DecimalMin("0.01") BigDecimal salePrice
) {}
