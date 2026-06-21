package com.smartclinic.modules.bloodbank.dto;

import com.smartclinic.modules.bloodbank.domain.BloodUnit.UnitStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUnitStatusRequest(@NotNull UnitStatus status, String notes) {}
