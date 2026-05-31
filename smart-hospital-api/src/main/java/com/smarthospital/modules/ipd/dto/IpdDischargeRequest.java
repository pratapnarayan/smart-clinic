package com.smarthospital.modules.ipd.dto;

import com.smarthospital.modules.ipd.domain.IpdAdmission.DischargeCondition;
import jakarta.validation.constraints.NotNull;

public record IpdDischargeRequest(
        @NotNull DischargeCondition conditionAtDischarge,
        String                      finalDiagnosis,
        String                      dischargeNotes,
        String                      followUpInstructions
) {}
