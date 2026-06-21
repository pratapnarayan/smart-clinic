package com.smartclinic.modules.pharmacy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BillItemRequest(
        @NotNull UUID batchId,
        @Min(1)  int  quantity
) {}
