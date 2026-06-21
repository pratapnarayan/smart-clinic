package com.smartclinic.modules.pharmacy.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record MedicineCreateRequest(
        @NotNull  UUID   categoryId,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 200) String genericName,
        @NotBlank @Size(max = 20)  String unit,
        @Min(0) int reorderLevel
) {}
