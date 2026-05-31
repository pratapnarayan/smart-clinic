package com.smarthospital.modules.operation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OtConsumableRequest(@NotNull UUID itemId, @Min(1) int quantityUsed) {}
