package com.smartclinic.modules.ipd.controller;

import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.ipd.domain.IpdAdmission.AdmissionStatus;
import com.smartclinic.modules.ipd.domain.IpdBed.BedStatus;
import com.smartclinic.modules.ipd.dto.*;
import com.smartclinic.modules.ipd.service.IpdService;
import com.smartclinic.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ipd")
@Tag(name = "IPD", description = "Inpatient admissions, bed management and discharge")
public class IpdController {

    private final IpdService ipdService;

    public IpdController(IpdService ipdService) {
        this.ipdService = ipdService;
    }

    // ── Wards ────────────────────────────────────────────────────────────────

    @GetMapping("/wards")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "List all active wards")
    public ResponseEntity<ApiResponse<List<WardResponse>>> listWards() {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.listWards()));
    }

    @PostMapping("/wards")
    @PreAuthorize("hasAuthority('IPD.MANAGE')")
    @Operation(summary = "Create a new ward")
    public ResponseEntity<ApiResponse<WardResponse>> createWard(@Valid @RequestBody WardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(ipdService.createWard(request)));
    }

    // ── Beds ─────────────────────────────────────────────────────────────────

    @GetMapping("/wards/{wardId}/beds")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "List all beds in a ward")
    public ResponseEntity<ApiResponse<List<BedResponse>>> listBeds(@PathVariable UUID wardId) {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.listBeds(wardId)));
    }

    @GetMapping("/wards/{wardId}/beds/available")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "List available beds in a ward")
    public ResponseEntity<ApiResponse<List<BedResponse>>> listAvailableBeds(@PathVariable UUID wardId) {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.listAvailableBeds(wardId)));
    }

    @PostMapping("/wards/{wardId}/beds")
    @PreAuthorize("hasAuthority('IPD.MANAGE')")
    @Operation(summary = "Add a bed to a ward")
    public ResponseEntity<ApiResponse<BedResponse>> addBed(
            @PathVariable UUID wardId,
            @Valid @RequestBody BedCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ipdService.addBed(wardId, request)));
    }

    @PatchMapping("/beds/{bedId}/status")
    @PreAuthorize("hasAuthority('IPD.MANAGE')")
    @Operation(summary = "Update bed status (AVAILABLE / MAINTENANCE)")
    public ResponseEntity<ApiResponse<BedResponse>> updateBedStatus(
            @PathVariable UUID bedId,
            @RequestParam BedStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.updateBedStatus(bedId, status)));
    }

    // ── Admissions ───────────────────────────────────────────────────────────

    @PostMapping("/admissions")
    @PreAuthorize("hasAuthority('IPD.CREATE')")
    @Operation(summary = "Admit a patient")
    public ResponseEntity<ApiResponse<IpdAdmissionResponse>> admitPatient(
            @Valid @RequestBody IpdAdmissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ipdService.admitPatient(request)));
    }

    @GetMapping("/admissions/{id}")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "Get admission details")
    public ResponseEntity<ApiResponse<IpdAdmissionResponse>> getAdmission(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.getAdmission(id)));
    }

    @GetMapping("/admissions")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "List admissions, optionally filtered by status")
    public ResponseEntity<ApiResponse<PageResponse<IpdAdmissionResponse>>> listAdmissions(
            @RequestParam(required = false) AdmissionStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("admissionDate").descending());
        if (status != null) {
            return ResponseEntity.ok(ApiResponse.ok(ipdService.listByStatus(status, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.ok(ipdService.listAdmissions(pageable)));
    }

    @GetMapping("/admissions/patient/{patientId}")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "List all admissions for a patient")
    public ResponseEntity<ApiResponse<PageResponse<IpdAdmissionResponse>>> listByPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("admissionDate").descending());
        return ResponseEntity.ok(ApiResponse.ok(ipdService.listByPatient(patientId, pageable)));
    }

    @PatchMapping("/admissions/{id}")
    @PreAuthorize("hasAuthority('IPD.EDIT')")
    @Operation(summary = "Update admission (diagnosis, notes, bed transfer, discount)")
    public ResponseEntity<ApiResponse<IpdAdmissionResponse>> updateAdmission(
            @PathVariable UUID id,
            @Valid @RequestBody IpdAdmissionUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.updateAdmission(id, request)));
    }

    @PostMapping("/admissions/{id}/discharge")
    @PreAuthorize("hasAuthority('IPD.EDIT')")
    @Operation(summary = "Discharge a patient")
    public ResponseEntity<ApiResponse<IpdAdmissionResponse>> discharge(
            @PathVariable UUID id,
            @Valid @RequestBody IpdDischargeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.dischargePatient(id, request)));
    }

    // ── Charges ──────────────────────────────────────────────────────────────

    @PostMapping("/admissions/{id}/charges")
    @PreAuthorize("hasAuthority('IPD.CREATE')")
    @Operation(summary = "Add a charge to an admission")
    public ResponseEntity<ApiResponse<IpdAdmissionResponse>> addCharge(
            @PathVariable UUID id,
            @Valid @RequestBody IpdChargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ipdService.addCharge(id, request)));
    }

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('IPD.VIEW')")
    @Operation(summary = "IPD dashboard — bed occupancy and admission counts")
    public ResponseEntity<ApiResponse<IpdDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(ipdService.getDashboard()));
    }
}
