package com.smartclinic.modules.opd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PrescriptionRequest(
        @Size(max = 1000) String advice,
        Integer followUpDays,
        @NotEmpty @Valid List<PrescriptionItemRequest> items
) {}
