package com.smartclinic.modules.radiology.dto;

import jakarta.validation.constraints.NotBlank;

public record ModalityRequest(@NotBlank String name, @NotBlank String code, String description) {}
