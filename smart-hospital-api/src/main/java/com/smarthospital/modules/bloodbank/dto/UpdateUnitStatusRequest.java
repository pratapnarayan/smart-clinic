package com.smarthospital.modules.bloodbank.dto;

import com.smarthospital.modules.bloodbank.domain.BloodUnit.UnitStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUnitStatusRequest(@NotNull UnitStatus status, String notes) {}
