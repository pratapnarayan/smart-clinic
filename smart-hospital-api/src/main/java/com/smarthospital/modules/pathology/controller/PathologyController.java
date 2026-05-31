package com.smarthospital.modules.pathology.controller;

import com.smarthospital.core.pagination.PageResponse;
import com.smarthospital.modules.pathology.domain.LabOrder.OrderStatus;
import com.smarthospital.modules.pathology.domain.LabOrder.PaymentStatus;
import com.smarthospital.modules.pathology.dto.*;
import com.smarthospital.modules.pathology.service.PathologyService;
import com.smarthospital.shared.dto.ApiResponse;
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
@RequestMapping("/api/v1/pathology")
@Tag(name = "Pathology", description = "Lab test catalog, orders and result reporting")
public class PathologyController {

    private final PathologyService service;

    public PathologyController(PathologyService service) {
        this.service = service;
    }

    // ── Categories ────────────────────────────────────────────────────────────

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "List active lab test categories")
    public ResponseEntity<ApiResponse<List<LabCategoryResponse>>> listCategories() {
        return ResponseEntity.ok(ApiResponse.ok(service.listCategories()));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('PATHOLOGY.MANAGE')")
    @Operation(summary = "Create a test category")
    public ResponseEntity<ApiResponse<LabCategoryResponse>> createCategory(
            @Valid @RequestBody LabCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createCategory(request)));
    }

    // ── Test Catalog ──────────────────────────────────────────────────────────

    @GetMapping("/tests")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "List lab tests, optionally filter by category")
    public ResponseEntity<ApiResponse<List<LabTestResponse>>> listTests(
            @RequestParam(required = false) UUID categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listTests(categoryId)));
    }

    @GetMapping("/tests/{id}")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "Get lab test details")
    public ResponseEntity<ApiResponse<LabTestResponse>> getTest(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getTest(id)));
    }

    @PostMapping("/tests")
    @PreAuthorize("hasAuthority('PATHOLOGY.MANAGE')")
    @Operation(summary = "Add a test to the catalog")
    public ResponseEntity<ApiResponse<LabTestResponse>> createTest(
            @Valid @RequestBody LabTestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createTest(request)));
    }

    @PatchMapping("/tests/{id}")
    @PreAuthorize("hasAuthority('PATHOLOGY.MANAGE')")
    @Operation(summary = "Update a test (price, normal range, turnaround)")
    public ResponseEntity<ApiResponse<LabTestResponse>> updateTest(
            @PathVariable UUID id,
            @Valid @RequestBody LabTestRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateTest(id, request)));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @PostMapping("/orders")
    @PreAuthorize("hasAuthority('PATHOLOGY.CREATE')")
    @Operation(summary = "Create a lab order for a patient")
    public ResponseEntity<ApiResponse<LabOrderResponse>> createOrder(
            @Valid @RequestBody LabOrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createOrder(request)));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "Get lab order details with all results")
    public ResponseEntity<ApiResponse<LabOrderResponse>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getOrder(id)));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "List orders, optionally filter by status")
    public ResponseEntity<ApiResponse<PageResponse<LabOrderResponse>>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listOrders(status, pageable)));
    }

    @GetMapping("/orders/patient/{patientId}")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "List all lab orders for a patient")
    public ResponseEntity<ApiResponse<PageResponse<LabOrderResponse>>> listByPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listByPatient(patientId, pageable)));
    }

    @PostMapping("/orders/{id}/collect-sample")
    @PreAuthorize("hasAuthority('PATHOLOGY.EDIT')")
    @Operation(summary = "Mark sample as collected — moves order to SAMPLE_COLLECTED")
    public ResponseEntity<ApiResponse<LabOrderResponse>> collectSample(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.collectSample(id)));
    }

    @PostMapping("/orders/{id}/start")
    @PreAuthorize("hasAuthority('PATHOLOGY.EDIT')")
    @Operation(summary = "Start processing — moves order to IN_PROGRESS")
    public ResponseEntity<ApiResponse<LabOrderResponse>> startProcessing(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.startProcessing(id)));
    }

    @PostMapping("/orders/{orderId}/items/{itemId}/result")
    @PreAuthorize("hasAuthority('PATHOLOGY.EDIT')")
    @Operation(summary = "Enter result for a single test item")
    public ResponseEntity<ApiResponse<LabOrderResponse>> enterResult(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ResultEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.enterResult(orderId, itemId, request)));
    }

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('PATHOLOGY.EDIT')")
    @Operation(summary = "Cancel a lab order")
    public ResponseEntity<ApiResponse<LabOrderResponse>> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.cancelOrder(id)));
    }

    @PatchMapping("/orders/{id}/payment")
    @PreAuthorize("hasAuthority('PATHOLOGY.EDIT')")
    @Operation(summary = "Update payment status of an order")
    public ResponseEntity<ApiResponse<LabOrderResponse>> updatePayment(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(service.updatePayment(id, status)));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('PATHOLOGY.VIEW')")
    @Operation(summary = "Pathology dashboard — order counts by status")
    public ResponseEntity<ApiResponse<PathologyDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(service.getDashboard()));
    }
}
