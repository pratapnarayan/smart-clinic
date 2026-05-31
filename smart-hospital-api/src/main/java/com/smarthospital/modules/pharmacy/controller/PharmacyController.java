package com.smarthospital.modules.pharmacy.controller;

import com.smarthospital.core.pagination.PageResponse;
import com.smarthospital.modules.pharmacy.dto.*;
import com.smarthospital.modules.pharmacy.service.PharmacyService;
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
@RequestMapping("/api/v1/pharmacy")
@Tag(name = "Pharmacy", description = "Medicine catalogue, stock management and billing")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    // ── Categories ──────────────────────────────────────────────────────────────

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "List all medicine categories")
    public ResponseEntity<ApiResponse<List<MedicineCategoryResponse>>> listCategories() {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.listCategories()));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('PHARMACY.CREATE')")
    @Operation(summary = "Create a medicine category")
    public ResponseEntity<ApiResponse<MedicineCategoryResponse>> createCategory(
            @Valid @RequestBody MedicineCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(pharmacyService.createCategory(req)));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('PHARMACY.CREATE')")
    @Operation(summary = "Rename a medicine category")
    public ResponseEntity<ApiResponse<MedicineCategoryResponse>> updateCategory(
            @PathVariable UUID id, @Valid @RequestBody MedicineCategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.updateCategory(id, req)));
    }

    // ── Medicines ───────────────────────────────────────────────────────────────

    @GetMapping("/medicines")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "Search medicines by name / filter by category")
    public ResponseEntity<ApiResponse<PageResponse<MedicineResponse>>> searchMedicines(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID   categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.searchMedicines(query, categoryId, pageable)));
    }

    @GetMapping("/medicines/{id}")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "Get medicine detail with current stock")
    public ResponseEntity<ApiResponse<MedicineResponse>> getMedicine(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.getMedicine(id)));
    }

    @PostMapping("/medicines")
    @PreAuthorize("hasAuthority('PHARMACY.CREATE')")
    @Operation(summary = "Add a new medicine to the catalogue")
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicine(
            @Valid @RequestBody MedicineCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(pharmacyService.createMedicine(req)));
    }

    @PutMapping("/medicines/{id}")
    @PreAuthorize("hasAuthority('PHARMACY.CREATE')")
    @Operation(summary = "Update medicine details")
    public ResponseEntity<ApiResponse<MedicineResponse>> updateMedicine(
            @PathVariable UUID id, @Valid @RequestBody MedicineCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.updateMedicine(id, req)));
    }

    @DeleteMapping("/medicines/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a medicine from the catalogue")
    public ResponseEntity<Void> deleteMedicine(@PathVariable UUID id) {
        pharmacyService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medicines/low-stock")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "List medicines at or below reorder level")
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.getLowStockMedicines()));
    }

    // ── Stock / Batches ─────────────────────────────────────────────────────────

    @GetMapping("/stock/{medicineId}")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "Get stock summary and all batches for a medicine")
    public ResponseEntity<ApiResponse<StockSummaryResponse>> getStock(@PathVariable UUID medicineId) {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.getStockSummary(medicineId)));
    }

    @PostMapping("/stock/batches")
    @PreAuthorize("hasAuthority('PHARMACY.CREATE')")
    @Operation(summary = "Add a new stock batch (purchase/goods receipt)")
    public ResponseEntity<ApiResponse<MedicineBatchResponse>> addBatch(
            @Valid @RequestBody MedicineBatchCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(pharmacyService.addBatch(req)));
    }

    @GetMapping("/stock/expiring")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "List batches expiring within N days (default 30)")
    public ResponseEntity<ApiResponse<List<MedicineBatchResponse>>> getExpiring(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.getExpiringBatches(days)));
    }

    // ── Billing ─────────────────────────────────────────────────────────────────

    @PostMapping("/bills")
    @PreAuthorize("hasAuthority('PHARMACY.CREATE')")
    @Operation(summary = "Create a pharmacy bill and deduct stock")
    public ResponseEntity<ApiResponse<BillResponse>> createBill(
            @Valid @RequestBody BillCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(pharmacyService.createBill(req)));
    }

    @GetMapping("/bills/{id}")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "Get pharmacy bill by ID")
    public ResponseEntity<ApiResponse<BillResponse>> getBill(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.getBill(id)));
    }

    @GetMapping("/bills/patient/{patientId}")
    @PreAuthorize("hasAuthority('PHARMACY.VIEW')")
    @Operation(summary = "List all pharmacy bills for a patient")
    public ResponseEntity<ApiResponse<PageResponse<BillResponse>>> listBillsByPatient(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(pharmacyService.listBillsByPatient(patientId, pageable)));
    }
}
