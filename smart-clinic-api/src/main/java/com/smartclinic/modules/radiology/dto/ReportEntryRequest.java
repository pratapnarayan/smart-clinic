package com.smartclinic.modules.radiology.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportEntryRequest(
        @NotBlank String findings,
        String           impression,
        String           reportedBy
) {}
