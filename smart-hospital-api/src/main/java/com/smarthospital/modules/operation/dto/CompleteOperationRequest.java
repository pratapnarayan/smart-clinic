package com.smarthospital.modules.operation.dto;

import com.smarthospital.modules.operation.domain.OtSchedule.AnesthesiaType;
import com.smarthospital.modules.operation.domain.OtSchedule.Outcome;
import com.smarthospital.modules.operation.domain.OtSchedule.PatientCondition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CompleteOperationRequest(
        @NotNull LocalDateTime actualStart,
        @NotNull LocalDateTime actualEnd,
        AnesthesiaType   anesthesiaType,
        String           postOpDiagnosis,
        String           procedureDetails,
        String           complications,
        String           surgeonNotes,
        @NotNull Outcome outcome,
        @NotNull PatientCondition patientConditionAfter,
        @Valid List<OtConsumableRequest> consumables
) {}
