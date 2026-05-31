package com.smarthospital.modules.pathology.dto;

import jakarta.validation.constraints.NotBlank;

public record ResultEntryRequest(@NotBlank String result, String resultNote, String enteredBy) {}
