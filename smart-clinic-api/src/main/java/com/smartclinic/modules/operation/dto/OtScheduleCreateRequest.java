package com.smartclinic.modules.operation.dto;

import com.smartclinic.modules.operation.domain.OtSchedule.OperationType;
import com.smartclinic.modules.operation.domain.OtSchedule.Priority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record OtScheduleCreateRequest(
        UUID          admissionId,
        UUID          patientId,
        String        patientName,
        @NotNull UUID theatreId,
        @NotNull LocalDate     scheduledDate,
        @NotNull LocalDateTime scheduledStart,
        @Min(15) int  estimatedDurationMins,
        @NotBlank String procedureName,
        OperationType operationType,
        Priority      priority,
        UUID          surgeonId,
        String        surgeonName,
        UUID          anesthetistId,
        String        anesthetistName,
        String        assistantNames,
        String        preOpDiagnosis,
        UUID          bloodRequestId,
        String        bloodRequestNumber,
        String        notes
) {}
