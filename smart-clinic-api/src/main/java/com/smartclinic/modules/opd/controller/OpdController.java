package com.smartclinic.modules.opd.controller;

import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.opd.dto.*;
import com.smartclinic.modules.opd.service.OpdService;
import com.smartclinic.shared.dto.ApiResponse;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/opd")
@Tag(name = "OPD", description = "Outpatient visits, prescriptions and billing")
public class OpdController {

    private final OpdService opdService;

    public OpdController(OpdService opdService) {
        this.opdService = opdService;
    }

    // ── Visits ──────────────────────────────────────────────────────────────────

    @PostMapping("/visits")
    @PreAuthorize("hasAuthority('OPD.CREATE')")
    @Operation(summary = "Register a new OPD visit")
    public ResponseEntity<ApiResponse<OpdVisitResponse>> createVisit(
            @Valid @RequestBody OpdVisitCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(opdService.createVisit(request)));
    }

    @GetMapping("/visits/{id}")
    @PreAuthorize("hasAuthority('OPD.VIEW')")
    @Operation(summary = "Get OPD visit details")
    public ResponseEntity<ApiResponse<OpdVisitResponse>> getVisit(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(opdService.getVisit(id)));
    }

    @GetMapping("/visits")
    @PreAuthorize("hasAuthority('OPD.VIEW')")
    @Operation(summary = "List today's visits, optionally filter by date")
    public ResponseEntity<ApiResponse<PageResponse<OpdVisitResponse>>> listByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return ResponseEntity.ok(ApiResponse.ok(opdService.listByDate(date, pageable)));
    }

    @GetMapping("/visits/queue")
    @PreAuthorize("hasAuthority('OPD.VIEW')")
    @Operation(summary = "Today's live OPD queue — REGISTERED and IN_PROGRESS visits only")
    public ResponseEntity<ApiResponse<List<OpdVisitResponse>>> getTodaysQueue() {
        return ResponseEntity.ok(ApiResponse.ok(opdService.getTodaysQueue()));
    }

    @GetMapping("/visits/patient/{patientId}")
    @PreAuthorize("hasAuthority('OPD.VIEW')")
    @Operation(summary = "List all visits for a patient")
    public ResponseEntity<ApiResponse<PageResponse<OpdVisitResponse>>> listByPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("visitDate").descending());
        return ResponseEntity.ok(ApiResponse.ok(opdService.listByPatient(patientId, pageable)));
    }

    @PatchMapping("/visits/{id}")
    @PreAuthorize("hasAuthority('OPD.EDIT')")
    @Operation(summary = "Update visit (diagnosis, notes, status, discount)")
    public ResponseEntity<ApiResponse<OpdVisitResponse>> updateVisit(
            @PathVariable UUID id,
            @Valid @RequestBody OpdVisitUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(opdService.updateVisit(id, request)));
    }

    @DeleteMapping("/visits/{id}")
    @PreAuthorize("hasAuthority('OPD.EDIT')")
    @Operation(summary = "Cancel a visit")
    public ResponseEntity<ApiResponse<Void>> cancelVisit(@PathVariable UUID id) {
        opdService.cancelVisit(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── Prescription ────────────────────────────────────────────────────────────

    @PutMapping("/visits/{visitId}/prescription")
    @PreAuthorize("hasAuthority('OPD.CREATE')")
    @Operation(summary = "Save (create or replace) prescription for a visit")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> savePrescription(
            @PathVariable UUID visitId,
            @Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(opdService.savePrescription(visitId, request)));
    }

    @GetMapping("/visits/{visitId}/prescription")
    @PreAuthorize("hasAuthority('OPD.VIEW')")
    @Operation(summary = "Get prescription for a visit")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescription(
            @PathVariable UUID visitId) {
        return ResponseEntity.ok(ApiResponse.ok(opdService.getPrescription(visitId)));
    }

    // ── Bill ────────────────────────────────────────────────────────────────────

    @GetMapping("/visits/{visitId}/bill")
    @PreAuthorize("hasAuthority('OPD.VIEW')")
    @Operation(summary = "Get bill summary for a visit")
    public ResponseEntity<ApiResponse<OpdVisitResponse>> getBill(@PathVariable UUID visitId) {
        return ResponseEntity.ok(ApiResponse.ok(opdService.getBill(visitId)));
    }
}
