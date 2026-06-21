package com.smartclinic.modules.patient.controller;

import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.patient.dto.PatientCreateRequest;
import com.smartclinic.modules.patient.dto.PatientResponse;
import com.smartclinic.modules.patient.service.PatientService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Patient registration, search and profile management")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PATIENT.VIEW')")
    @Operation(summary = "List / search patients (FTS supported)")
    public ResponseEntity<ApiResponse<PageResponse<PatientResponse>>> list(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(patientService.search(query, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT.VIEW')")
    @Operation(summary = "Get patient detail by ID")
    public ResponseEntity<ApiResponse<PatientResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT.CREATE')")
    @Operation(summary = "Register a new patient")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody PatientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(patientService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT.EDIT')")
    @Operation(summary = "Update patient details")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PatientCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete patient record")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
