package com.smarthospital.modules.operation.controller;

import com.smarthospital.core.pagination.PageResponse;
import com.smarthospital.modules.operation.domain.OtSchedule.Status;
import com.smarthospital.modules.operation.dto.*;
import com.smarthospital.modules.operation.service.OperationService;
import com.smarthospital.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operation")
@Tag(name = "Operation Theatre", description = "OT scheduling, team assignment and post-op records")
public class OperationController {

    private final OperationService service;

    public OperationController(OperationService service) {
        this.service = service;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('OPERATION.VIEW')")
    @Operation(summary = "OT dashboard — today's schedule and theatre utilization")
    public ResponseEntity<ApiResponse<OtDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(service.getDashboard()));
    }

    // ── Theatres ──────────────────────────────────────────────────────────────

    @GetMapping("/theatres")
    @PreAuthorize("hasAuthority('OPERATION.VIEW')")
    @Operation(summary = "List active OT rooms")
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> listTheatres() {
        return ResponseEntity.ok(ApiResponse.ok(service.listTheatres()));
    }

    @PostMapping("/theatres")
    @PreAuthorize("hasAuthority('OPERATION.MANAGE')")
    @Operation(summary = "Add a new operation theatre")
    public ResponseEntity<ApiResponse<TheatreResponse>> createTheatre(
            @Valid @RequestBody TheatreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createTheatre(request)));
    }

    // ── Schedules ─────────────────────────────────────────────────────────────

    @PostMapping("/schedules")
    @PreAuthorize("hasAuthority('OPERATION.CREATE')")
    @Operation(summary = "Schedule an operation")
    public ResponseEntity<ApiResponse<OtScheduleResponse>> scheduleOperation(
            @Valid @RequestBody OtScheduleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.scheduleOperation(request)));
    }

    @GetMapping("/schedules/{id}")
    @PreAuthorize("hasAuthority('OPERATION.VIEW')")
    @Operation(summary = "Get full schedule details including post-op and consumables")
    public ResponseEntity<ApiResponse<OtScheduleResponse>> getSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getSchedule(id)));
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasAuthority('OPERATION.VIEW')")
    @Operation(summary = "List schedules — filter by date, status, or theatre")
    public ResponseEntity<ApiResponse<PageResponse<OtScheduleResponse>>> listSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) UUID   theatreId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("scheduledStart").ascending());
        return ResponseEntity.ok(ApiResponse.ok(service.listSchedules(date, status, theatreId, pageable)));
    }

    @PostMapping("/schedules/{id}/start")
    @PreAuthorize("hasAuthority('OPERATION.EDIT')")
    @Operation(summary = "Mark operation as started (SCHEDULED → IN_PROGRESS)")
    public ResponseEntity<ApiResponse<OtScheduleResponse>> startOperation(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.startOperation(id)));
    }

    @PostMapping("/schedules/{id}/complete")
    @PreAuthorize("hasAuthority('OPERATION.EDIT')")
    @Operation(summary = "Complete operation — records post-op details and deducts consumables from inventory")
    public ResponseEntity<ApiResponse<OtScheduleResponse>> completeOperation(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteOperationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.completeOperation(id, request)));
    }

    @PostMapping("/schedules/{id}/postpone")
    @PreAuthorize("hasAuthority('OPERATION.EDIT')")
    @Operation(summary = "Postpone a scheduled operation")
    public ResponseEntity<ApiResponse<OtScheduleResponse>> postponeOperation(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(ApiResponse.ok(service.postponeOperation(id, notes)));
    }

    @DeleteMapping("/schedules/{id}")
    @PreAuthorize("hasAuthority('OPERATION.EDIT')")
    @Operation(summary = "Cancel an operation")
    public ResponseEntity<ApiResponse<OtScheduleResponse>> cancelOperation(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(ApiResponse.ok(service.cancelOperation(id, notes)));
    }
}
