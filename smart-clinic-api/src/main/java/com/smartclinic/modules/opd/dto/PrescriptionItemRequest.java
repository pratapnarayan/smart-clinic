package com.smartclinic.modules.opd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PrescriptionItemRequest(
        @NotBlank @Size(max = 200) String medicineName,
        @NotBlank @Size(max = 50)  String dose,
        @NotBlank @Size(max = 80)  String frequency,
        @NotBlank @Size(max = 50)  String duration,
        @Size(max = 200)           String instructions
) {}
