package com.smarthospital.modules.operation.dto;

import com.smarthospital.modules.operation.domain.OtSchedule;
import com.smarthospital.modules.operation.domain.OtSchedule.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OtScheduleResponse(
        UUID            id,
        String          scheduleNumber,
        UUID            admissionId,
        UUID            patientId,
        String          patientName,
        UUID            theatreId,
        String          theatreName,
        LocalDate       scheduledDate,
        LocalDateTime   scheduledStart,
        int             estimatedDurationMins,
        String          procedureName,
        OperationType   operationType,
        Priority        priority,
        Status          status,
        UUID            surgeonId,
        String          surgeonName,
        UUID            anesthetistId,
        String          anesthetistName,
        String          assistantNames,
        String          preOpDiagnosis,
        UUID            bloodRequestId,
        String          bloodRequestNumber,
        String          notes,
        // Post-op (null until completed)
        LocalDateTime   actualStart,
        LocalDateTime   actualEnd,
        AnesthesiaType  anesthesiaType,
        String          postOpDiagnosis,
        String          procedureDetails,
        String          complications,
        String          surgeonNotes,
        Outcome         outcome,
        PatientCondition patientConditionAfter,
        List<OtConsumableResponse> consumables,
        Instant         createdAt
) {
    public static OtScheduleResponse from(OtSchedule s) {
        return new OtScheduleResponse(
                s.getId(), s.getScheduleNumber(),
                s.getAdmissionId(), s.getPatientId(), s.getPatientName(),
                s.getTheatreId(), s.getTheatreName(),
                s.getScheduledDate(), s.getScheduledStart(), s.getEstimatedDurationMins(),
                s.getProcedureName(), s.getOperationType(), s.getPriority(), s.getStatus(),
                s.getSurgeonId(), s.getSurgeonName(),
                s.getAnesthetistId(), s.getAnesthetistName(),
                s.getAssistantNames(), s.getPreOpDiagnosis(),
                s.getBloodRequestId(), s.getBloodRequestNumber(),
                s.getNotes(),
                s.getActualStart(), s.getActualEnd(),
                s.getAnesthesiaType(), s.getPostOpDiagnosis(),
                s.getProcedureDetails(), s.getComplications(), s.getSurgeonNotes(),
                s.getOutcome(), s.getPatientConditionAfter(),
                s.getConsumables().stream().map(OtConsumableResponse::from).toList(),
                s.getCreatedAt()
        );
    }
}
