package com.smartclinic.modules.ipd.dto;

import com.smartclinic.modules.ipd.domain.IpdAdmission.DischargeCondition;
import jakarta.validation.constraints.NotNull;

public record IpdDischargeRequest(
        @NotNull DischargeCondition conditionAtDischarge,
        String                      finalDiagnosis,
        String                      dischargeNotes,
        String                      followUpInstructions
) {}
