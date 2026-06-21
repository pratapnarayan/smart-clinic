package com.smartclinic.modules.analytics;

import com.smartclinic.modules.analytics.dto.*;
import com.smartclinic.modules.doctor.service.DoctorAnalyticsService;
import com.smartclinic.modules.finance.service.FinanceAnalyticsService;
import com.smartclinic.modules.frontoffice.repository.AppointmentRepository;
import com.smartclinic.modules.frontoffice.service.AppointmentAnalyticsService;
import com.smartclinic.modules.inventory.service.InventoryAnalyticsService;
import com.smartclinic.modules.pathology.service.PathologyAnalyticsService;
import com.smartclinic.modules.patient.service.PatientAnalyticsService;
import com.smartclinic.modules.pharmacy.service.PharmacyAnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExecutiveDashboardService {

    private final FinanceAnalyticsService financeAnalytics;
    private final PatientAnalyticsService patientAnalytics;
    private final DoctorAnalyticsService doctorAnalytics;
    private final AppointmentAnalyticsService appointmentAnalytics;
    private final PharmacyAnalyticsService pharmacyAnalytics;
    private final PathologyAnalyticsService pathologyAnalytics;
    private final InventoryAnalyticsService inventoryAnalytics;
    private final AppointmentRepository appointmentRepo;

    public ExecutiveDashboardService(FinanceAnalyticsService financeAnalytics,
                                     PatientAnalyticsService patientAnalytics,
                                     DoctorAnalyticsService doctorAnalytics,
                                     AppointmentAnalyticsService appointmentAnalytics,
                                     PharmacyAnalyticsService pharmacyAnalytics,
                                     PathologyAnalyticsService pathologyAnalytics,
                                     InventoryAnalyticsService inventoryAnalytics,
                                     AppointmentRepository appointmentRepo) {
        this.financeAnalytics    = financeAnalytics;
        this.patientAnalytics    = patientAnalytics;
        this.doctorAnalytics     = doctorAnalytics;
        this.appointmentAnalytics = appointmentAnalytics;
        this.pharmacyAnalytics   = pharmacyAnalytics;
        this.pathologyAnalytics  = pathologyAnalytics;
        this.inventoryAnalytics  = inventoryAnalytics;
        this.appointmentRepo     = appointmentRepo;
    }

    public ExecutiveDashboardResponse getDashboard() {
        LocalDate today        = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(29);

        // KPI scalars
        var todayRevenue      = financeAnalytics.getTodayRevenue();
        var monthRevenue      = financeAnalytics.getMonthRevenue();
        long totalPatients    = patientAnalytics.getTotalPatients();
        long todayAppts       = appointmentAnalytics.getTodayAppointments();
        var pendingPayments   = financeAnalytics.getPendingPayments();
        long doctorsAvailable = doctorAnalytics.getDoctorsAvailableToday();
        long currentAdmissions = getActiveIpdAdmissions();
        long labTestsToday    = pathologyAnalytics.getLabTestsToday();
        var medicineSales     = pharmacyAnalytics.getMedicineSalesToday();
        long inventoryAlerts  = inventoryAnalytics.getInventoryAlerts();

        // 30-day analytics for chart data
        var financeData = financeAnalytics.getAnalytics(thirtyDaysAgo, today);
        var patientData = patientAnalytics.getAnalytics(thirtyDaysAgo, today);
        var doctorData  = doctorAnalytics.getAnalytics(thirtyDaysAgo, today);

        return new ExecutiveDashboardResponse(
                todayRevenue,
                monthRevenue,
                totalPatients,
                todayAppts,
                pendingPayments,
                doctorsAvailable,
                currentAdmissions,
                labTestsToday,
                medicineSales,
                inventoryAlerts,
                null,   // todayRevenueTrend — future enhancement
                null,   // monthRevenueTrend — future enhancement
                null,   // totalPatientsTrend — future enhancement
                null,   // todayAppointmentsTrend — future enhancement
                financeData.dailyRevenue(),
                patientData.registrationTrend(),
                financeData.revenueBySource(),
                doctorData.revenueByDoctor(),
                List.of()  // departmentRevenue — no cross-module aggregation yet; wired in a follow-up
        );
    }

    /**
     * Approximation of current IPD admissions using appointments with CHECKED_IN status
     * over the last 30 days. A dedicated IPD module would provide an exact count,
     * but cross-module dependency is out of scope for this task.
     */
    private long getActiveIpdAdmissions() {
        return appointmentRepo.countByStatusAndDateRange(
                "CHECKED_IN",
                LocalDate.now().minusDays(30),
                LocalDate.now());
    }
}
