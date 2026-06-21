package com.smartclinic.modules.inventory.controller;

import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.inventory.dto.*;
import com.smartclinic.modules.inventory.service.InventoryService;
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
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Item catalogue, stock receipts and issues")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "Inventory dashboard — counts, low stock, today activity")
    public ResponseEntity<ApiResponse<InventoryDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(service.getDashboard()));
    }

    // ── Categories ────────────────────────────────────────────────────────────

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "List active item categories")
    public ResponseEntity<ApiResponse<List<ItemCategoryResponse>>> listCategories() {
        return ResponseEntity.ok(ApiResponse.ok(service.listCategories()));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('INVENTORY.MANAGE')")
    @Operation(summary = "Create an item category")
    public ResponseEntity<ApiResponse<ItemCategoryResponse>> createCategory(
            @Valid @RequestBody ItemCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createCategory(request)));
    }

    // ── Items ─────────────────────────────────────────────────────────────────

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "List items — filter by search query, category, or low stock flag")
    public ResponseEntity<ApiResponse<PageResponse<InventoryItemResponse>>> listItems(
            @RequestParam(required = false) String  q,
            @RequestParam(required = false) UUID    categoryId,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(ApiResponse.ok(service.listItems(q, categoryId, lowStock, pageable)));
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "Get item details with current stock")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> getItem(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getItem(id)));
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('INVENTORY.CREATE')")
    @Operation(summary = "Add an item to the catalogue")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> createItem(
            @Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createItem(request)));
    }

    @PatchMapping("/items/{id}")
    @PreAuthorize("hasAuthority('INVENTORY.CREATE')")
    @Operation(summary = "Update item details (code, name, category, reorder level)")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateItem(id, request)));
    }

    // ── Stock Receipts ────────────────────────────────────────────────────────

    @PostMapping("/receipts")
    @PreAuthorize("hasAuthority('INVENTORY.CREATE')")
    @Operation(summary = "Record a goods receipt — increments item stock")
    public ResponseEntity<ApiResponse<StockReceiptResponse>> recordReceipt(
            @Valid @RequestBody StockReceiptRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.recordReceipt(request)));
    }

    @GetMapping("/receipts/{id}")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "Get receipt details")
    public ResponseEntity<ApiResponse<StockReceiptResponse>> getReceipt(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getReceipt(id)));
    }

    @GetMapping("/receipts")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "List receipts filtered by date range and optionally by item")
    public ResponseEntity<ApiResponse<PageResponse<StockReceiptResponse>>> listReceipts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID itemId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listReceipts(from, to, itemId, pageable)));
    }

    // ── Stock Issues ──────────────────────────────────────────────────────────

    @PostMapping("/issues")
    @PreAuthorize("hasAuthority('INVENTORY.CREATE')")
    @Operation(summary = "Record a stock issue to a department — decrements item stock")
    public ResponseEntity<ApiResponse<StockIssueResponse>> recordIssue(
            @Valid @RequestBody StockIssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.recordIssue(request)));
    }

    @GetMapping("/issues/{id}")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "Get issue details")
    public ResponseEntity<ApiResponse<StockIssueResponse>> getIssue(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getIssue(id)));
    }

    @GetMapping("/issues")
    @PreAuthorize("hasAuthority('INVENTORY.VIEW')")
    @Operation(summary = "List issues filtered by date range and optionally by item")
    public ResponseEntity<ApiResponse<PageResponse<StockIssueResponse>>> listIssues(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID itemId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listIssues(from, to, itemId, pageable)));
    }
}
