package com.smarthospital.modules.analytics;

import com.smarthospital.core.export.ExcelExportUtil;
import com.smarthospital.core.export.PdfExportUtil;
import com.smarthospital.core.security.UserPrincipal;
import com.smarthospital.modules.analytics.dto.*;
import com.smarthospital.modules.auth.domain.Permission;
import com.smarthospital.modules.doctor.service.DoctorAnalyticsService;
import com.smarthospital.modules.finance.service.FinanceAnalyticsService;
import com.smarthospital.modules.frontoffice.service.AppointmentAnalyticsService;
import com.smarthospital.modules.inventory.service.InventoryAnalyticsService;
import com.smarthospital.modules.pathology.service.PathologyAnalyticsService;
import com.smarthospital.modules.patient.service.PatientAnalyticsService;
import com.smarthospital.modules.pharmacy.service.PharmacyAnalyticsService;
import com.smarthospital.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Reports and analytics dashboards")
public class AnalyticsController {

    private final ExecutiveDashboardService executiveDashboardService;
    private final FinanceAnalyticsService financeAnalytics;
    private final PatientAnalyticsService patientAnalytics;
    private final DoctorAnalyticsService doctorAnalytics;
    private final AppointmentAnalyticsService appointmentAnalytics;
    private final PharmacyAnalyticsService pharmacyAnalytics;
    private final PathologyAnalyticsService pathologyAnalytics;
    private final InventoryAnalyticsService inventoryAnalytics;

    public AnalyticsController(
            ExecutiveDashboardService executiveDashboardService,
            FinanceAnalyticsService financeAnalytics,
            PatientAnalyticsService patientAnalytics,
            DoctorAnalyticsService doctorAnalytics,
            AppointmentAnalyticsService appointmentAnalytics,
            PharmacyAnalyticsService pharmacyAnalytics,
            PathologyAnalyticsService pathologyAnalytics,
            InventoryAnalyticsService inventoryAnalytics) {
        this.executiveDashboardService = executiveDashboardService;
        this.financeAnalytics = financeAnalytics;
        this.patientAnalytics = patientAnalytics;
        this.doctorAnalytics = doctorAnalytics;
        this.appointmentAnalytics = appointmentAnalytics;
        this.pharmacyAnalytics = pharmacyAnalytics;
        this.pathologyAnalytics = pathologyAnalytics;
        this.inventoryAnalytics = inventoryAnalytics;
    }

    @GetMapping("/executive")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Executive dashboard — aggregated KPIs and charts")
    public ResponseEntity<ApiResponse<ExecutiveDashboardResponse>> executive() {
        return ResponseEntity.ok(ApiResponse.ok(executiveDashboardService.getDashboard()));
    }

    @GetMapping("/finance")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Financial analytics for a date range")
    public ResponseEntity<ApiResponse<FinanceAnalyticsResponse>> finance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(financeAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Patient analytics for a date range")
    public ResponseEntity<ApiResponse<PatientAnalyticsResponse>> patients(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(patientAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Doctor analytics for a date range")
    public ResponseEntity<ApiResponse<DoctorAnalyticsResponse>> doctors(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(doctorAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Appointment analytics for a date range")
    public ResponseEntity<ApiResponse<AppointmentAnalyticsResponse>> appointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(appointmentAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/pharmacy")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Pharmacy analytics for a date range")
    public ResponseEntity<ApiResponse<PharmacyAnalyticsResponse>> pharmacy(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(pharmacyAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/laboratory")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Laboratory analytics for a date range")
    public ResponseEntity<ApiResponse<LaboratoryAnalyticsResponse>> laboratory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(pathologyAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Inventory analytics for a date range")
    public ResponseEntity<ApiResponse<InventoryAnalyticsResponse>> inventory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(ApiResponse.ok(inventoryAnalytics.getAnalytics(range[0], range[1])));
    }

    @GetMapping("/export/{section}")
    @PreAuthorize("hasAuthority('" + Permission.REPORTS_VIEW + "')")
    @Operation(summary = "Export analytics section as Excel or PDF")
    public ResponseEntity<byte[]> export(
            @PathVariable String section,
            @RequestParam(defaultValue = "excel") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal UserPrincipal user) {

        LocalDate[] range = resolveRange(from, to);
        String generatedBy = user != null ? user.getUsername() : "System";
        String title = section.substring(0, 1).toUpperCase() + section.substring(1) + " Analytics";
        String dateRange = range[0] + " to " + range[1];

        // Build generic export rows based on section
        List<String> headers = List.of("Metric", "Value", "Period");
        List<List<Object>> rows = buildExportRows(section, range[0], range[1]);

        byte[] data;
        String mediaType;
        String filename;

        if ("pdf".equalsIgnoreCase(format)) {
            data = PdfExportUtil.build(title + " (" + dateRange + ")", headers, rows, generatedBy);
            mediaType = "application/pdf";
            filename = section + "-analytics.pdf";
        } else {
            data = ExcelExportUtil.build(title + " (" + dateRange + ")", headers, rows, generatedBy);
            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            filename = section + "-analytics.xlsx";
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", mediaType)
                .body(data);
    }

    private List<List<Object>> buildExportRows(String section, LocalDate from, LocalDate to) {
        String period = from + " to " + to;
        return switch (section.toLowerCase()) {
            case "finance" -> {
                var d = financeAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Total Revenue", d.totalRevenue(), period),
                    List.of("Total Expenses", d.totalExpenses(), period),
                    List.of("Net Profit", d.netProfit(), period),
                    List.of("Collection Efficiency %", d.collectionEfficiencyPct(), period)
                );
            }
            case "patients" -> {
                var d = patientAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Total Patients", d.totalPatients(), period),
                    List.of("New Patients", d.newPatientsThisPeriod(), period),
                    List.of("Returning Patients", d.returningPatients(), period),
                    List.of("Retention Rate %", d.retentionRatePct(), period)
                );
            }
            case "doctors" -> {
                var d = doctorAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Total Doctors", d.totalDoctors(), period),
                    List.of("Active Doctors", d.activeDoctors(), period)
                );
            }
            case "appointments" -> {
                var d = appointmentAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Total Appointments", d.totalAppointments(), period),
                    List.of("Completed", d.completed(), period),
                    List.of("Cancelled", d.cancelled(), period),
                    List.of("No Show", d.noShow(), period)
                );
            }
            case "pharmacy" -> {
                var d = pharmacyAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Total Revenue", d.totalMedicineRevenue(), period),
                    List.of("Bills Issued", d.totalBillsIssued(), period),
                    List.of("Low Stock Alerts", d.lowStockAlerts(), period),
                    List.of("Expiry Alerts", d.expiryAlerts(), period)
                );
            }
            case "laboratory" -> {
                var d = pathologyAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Tests Performed", d.totalTestsPerformed(), period),
                    List.of("Total Revenue", d.totalRevenue(), period),
                    List.of("Pending Reports", d.pendingReports(), period)
                );
            }
            case "inventory" -> {
                var d = inventoryAnalytics.getAnalytics(from, to);
                yield List.of(
                    List.of("Total Stock Value", d.totalStockValue(), period),
                    List.of("Low Stock Items", d.lowStockItems(), period),
                    List.of("Out of Stock", d.outOfStockItems(), period),
                    List.of("Total Items", d.totalItems(), period)
                );
            }
            default -> {
                var exec = executiveDashboardService.getDashboard();
                yield List.of(
                    List.of("Today's Revenue", exec.todayRevenue(), "Today"),
                    List.of("Month Revenue", exec.monthRevenue(), "This Month"),
                    List.of("Total Patients", exec.totalPatients(), "All Time"),
                    List.of("Today's Appointments", exec.todayAppointments(), "Today")
                );
            }
        };
    }

    // Default range: last 30 days
    private LocalDate[] resolveRange(LocalDate from, LocalDate to) {
        LocalDate end   = to   != null ? to   : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(29);
        return new LocalDate[]{start, end};
    }
}
