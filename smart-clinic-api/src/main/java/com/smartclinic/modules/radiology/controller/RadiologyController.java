package com.smartclinic.modules.radiology.controller;

import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.radiology.domain.RadiologyOrder.OrderStatus;
import com.smartclinic.modules.radiology.domain.RadiologyOrder.PaymentStatus;
import com.smartclinic.modules.radiology.dto.*;
import com.smartclinic.modules.radiology.service.RadiologyService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/radiology")
@Tag(name = "Radiology", description = "Imaging studies, orders and radiological reports")
public class RadiologyController {

    private final RadiologyService service;

    public RadiologyController(RadiologyService service) {
        this.service = service;
    }

    // ── Modalities ────────────────────────────────────────────────────────────

    @GetMapping("/modalities")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "List active imaging modalities")
    public ResponseEntity<ApiResponse<List<ModalityResponse>>> listModalities() {
        return ResponseEntity.ok(ApiResponse.ok(service.listModalities()));
    }

    @PostMapping("/modalities")
    @PreAuthorize("hasAuthority('RADIOLOGY.MANAGE')")
    @Operation(summary = "Create an imaging modality")
    public ResponseEntity<ApiResponse<ModalityResponse>> createModality(
            @Valid @RequestBody ModalityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createModality(request)));
    }

    // ── Studies ───────────────────────────────────────────────────────────────

    @GetMapping("/studies")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "List studies, optionally filter by modality")
    public ResponseEntity<ApiResponse<List<StudyResponse>>> listStudies(
            @RequestParam(required = false) UUID modalityId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listStudies(modalityId)));
    }

    @GetMapping("/studies/{id}")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "Get study details")
    public ResponseEntity<ApiResponse<StudyResponse>> getStudy(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getStudy(id)));
    }

    @PostMapping("/studies")
    @PreAuthorize("hasAuthority('RADIOLOGY.MANAGE')")
    @Operation(summary = "Add a study to the catalog")
    public ResponseEntity<ApiResponse<StudyResponse>> createStudy(
            @Valid @RequestBody StudyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createStudy(request)));
    }

    @PatchMapping("/studies/{id}")
    @PreAuthorize("hasAuthority('RADIOLOGY.MANAGE')")
    @Operation(summary = "Update a study (price, prep instructions)")
    public ResponseEntity<ApiResponse<StudyResponse>> updateStudy(
            @PathVariable UUID id,
            @Valid @RequestBody StudyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateStudy(id, request)));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @PostMapping("/orders")
    @PreAuthorize("hasAuthority('RADIOLOGY.CREATE')")
    @Operation(summary = "Create a radiology order for a patient")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> createOrder(
            @Valid @RequestBody RadiologyOrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createOrder(request)));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "Get order details with all study reports")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getOrder(id)));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "List orders, optionally filter by status")
    public ResponseEntity<ApiResponse<PageResponse<RadiologyOrderResponse>>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listOrders(status, pageable)));
    }

    @GetMapping("/orders/patient/{patientId}")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "List all radiology orders for a patient")
    public ResponseEntity<ApiResponse<PageResponse<RadiologyOrderResponse>>> listByPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listByPatient(patientId, pageable)));
    }

    @PostMapping("/orders/{id}/schedule")
    @PreAuthorize("hasAuthority('RADIOLOGY.EDIT')")
    @Operation(summary = "Schedule an order for a specific date/time")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> scheduleOrder(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        return ResponseEntity.ok(ApiResponse.ok(service.scheduleOrder(id, scheduledAt)));
    }

    @PostMapping("/orders/{id}/start")
    @PreAuthorize("hasAuthority('RADIOLOGY.EDIT')")
    @Operation(summary = "Start imaging — moves order to IN_PROGRESS")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> startOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.startOrder(id)));
    }

    @PostMapping("/orders/{orderId}/items/{itemId}/report")
    @PreAuthorize("hasAuthority('RADIOLOGY.EDIT')")
    @Operation(summary = "Enter radiological report (findings + impression) for a study item")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> enterReport(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ReportEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.enterReport(orderId, itemId, request)));
    }

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('RADIOLOGY.EDIT')")
    @Operation(summary = "Cancel a radiology order")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.cancelOrder(id)));
    }

    @PatchMapping("/orders/{id}/payment")
    @PreAuthorize("hasAuthority('RADIOLOGY.EDIT')")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> updatePayment(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(service.updatePayment(id, status)));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('RADIOLOGY.VIEW')")
    @Operation(summary = "Radiology dashboard — order counts by status")
    public ResponseEntity<ApiResponse<RadiologyDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(service.getDashboard()));
    }
}
