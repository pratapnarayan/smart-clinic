package com.smarthospital.modules.finance.controller;

import com.smarthospital.core.pagination.PageResponse;
import com.smarthospital.modules.finance.domain.IncomeEntry.SourceType;
import com.smarthospital.modules.finance.dto.*;
import com.smarthospital.modules.finance.service.FinanceService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance")
@Tag(name = "Finance", description = "Income, expenses, and financial reporting")
public class FinanceController {

    private final FinanceService service;

    public FinanceController(FinanceService service) {
        this.service = service;
    }

    // ── Dashboard & Summary ───────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "Finance dashboard — today and month-to-date summary")
    public ResponseEntity<ApiResponse<FinanceDashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(service.getDashboard()));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "Income vs expense summary for a date range")
    public ResponseEntity<ApiResponse<PeriodSummaryResponse>> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(service.getSummary(from, to)));
    }

    // ── Expense Categories ────────────────────────────────────────────────────

    @GetMapping("/expense-categories")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "List active expense categories")
    public ResponseEntity<ApiResponse<List<ExpenseCategoryResponse>>> listCategories() {
        return ResponseEntity.ok(ApiResponse.ok(service.listCategories()));
    }

    @PostMapping("/expense-categories")
    @PreAuthorize("hasAuthority('FINANCE.MANAGE')")
    @Operation(summary = "Create an expense category")
    public ResponseEntity<ApiResponse<ExpenseCategoryResponse>> createCategory(
            @Valid @RequestBody ExpenseCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createCategory(request)));
    }

    // ── Income Entries ────────────────────────────────────────────────────────

    @PostMapping("/income")
    @PreAuthorize("hasAuthority('FINANCE.CREATE')")
    @Operation(summary = "Record a new income entry")
    public ResponseEntity<ApiResponse<IncomeEntryResponse>> createIncome(
            @Valid @RequestBody IncomeEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createIncome(request)));
    }

    @GetMapping("/income/{id}")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "Get income entry details")
    public ResponseEntity<ApiResponse<IncomeEntryResponse>> getIncome(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getIncome(id)));
    }

    @GetMapping("/income")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "List income entries, optionally filtered by date range and source type")
    public ResponseEntity<ApiResponse<PageResponse<IncomeEntryResponse>>> listIncome(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) SourceType sourceType,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listIncome(from, to, sourceType, pageable)));
    }

    // ── Expense Entries ───────────────────────────────────────────────────────

    @PostMapping("/expenses")
    @PreAuthorize("hasAuthority('FINANCE.CREATE')")
    @Operation(summary = "Record a new expense entry")
    public ResponseEntity<ApiResponse<ExpenseEntryResponse>> createExpense(
            @Valid @RequestBody ExpenseEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.createExpense(request)));
    }

    @GetMapping("/expenses/{id}")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "Get expense entry details")
    public ResponseEntity<ApiResponse<ExpenseEntryResponse>> getExpense(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getExpense(id)));
    }

    @GetMapping("/expenses")
    @PreAuthorize("hasAuthority('FINANCE.VIEW')")
    @Operation(summary = "List expense entries, optionally filtered by date range and category")
    public ResponseEntity<ApiResponse<PageResponse<ExpenseEntryResponse>>> listExpenses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());
        return ResponseEntity.ok(ApiResponse.ok(service.listExpenses(from, to, categoryId, pageable)));
    }
}
