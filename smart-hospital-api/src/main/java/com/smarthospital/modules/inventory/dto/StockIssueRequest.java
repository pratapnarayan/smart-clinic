package com.smarthospital.modules.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record StockIssueRequest(
        LocalDate  issueDate,
        @NotNull   UUID   itemId,
        @Min(1)    int    quantity,
        @NotBlank  String issuedTo,
        String     issuedBy,
        String     purpose,
        String     notes
) {}
