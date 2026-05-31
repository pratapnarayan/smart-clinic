package com.smarthospital.modules.opd.service;

import com.smarthospital.core.exception.ApiException;
import com.smarthospital.core.pagination.PageResponse;
import com.smarthospital.modules.opd.domain.OpdCharge;
import com.smarthospital.modules.opd.domain.OpdVisit;
import com.smarthospital.modules.opd.domain.OpdVisit.VisitStatus;
import com.smarthospital.modules.opd.domain.Prescription;
import com.smarthospital.modules.opd.domain.PrescriptionItem;
import com.smarthospital.modules.opd.dto.*;
import com.smarthospital.modules.opd.repository.OpdVisitRepository;
import com.smarthospital.modules.opd.repository.PrescriptionRepository;
import com.smarthospital.modules.patient.domain.Patient;
import com.smarthospital.modules.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OpdService {

    private static final Logger log = LoggerFactory.getLogger(OpdService.class);

    private final OpdVisitRepository     visitRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository      patientRepository;  // cross-module: UUID lookup only

    public OpdService(OpdVisitRepository     visitRepository,
                      PrescriptionRepository prescriptionRepository,
                      PatientRepository      patientRepository) {
        this.visitRepository      = visitRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository    = patientRepository;
    }

    // ── Visit CRUD ──────────────────────────────────────────────────────────────

    @Transactional
    public OpdVisitResponse createVisit(OpdVisitCreateRequest req) {
        // Validate patient exists — cross-module call via repository (UUID only, no JPA join)
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> ApiException.notFound("PATIENT_NOT_FOUND",
                        "Patient " + req.patientId() + " not found"));

        LocalDate visitDate = req.visitDate() != null ? req.visitDate() : LocalDate.now();

        OpdVisit visit = OpdVisit.builder()
                .visitNumber(generateVisitNumber(visitDate))
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .visitDate(visitDate)
                .department(req.department())
                .doctorId(req.doctorId())
                .doctorName(req.doctorName())
                .symptoms(req.symptoms())
                .consultationFee(req.consultationFee() != null ? req.consultationFee() : BigDecimal.ZERO)
                .build();

        if (req.charges() != null) {
            req.charges().forEach(c ->
                    visit.getCharges().add(new OpdCharge(visit, c.description(), c.amount(), c.category()))
            );
        }
        visit.recalculateTotals();

        OpdVisit saved = visitRepository.save(visit);
        log.info("OPD visit {} created for patient {}", saved.getVisitNumber(), patient.getId());
        return OpdVisitResponse.from(saved);
    }

    public OpdVisitResponse getVisit(UUID id) {
        return visitRepository.findById(id)
                .map(OpdVisitResponse::from)
                .orElseThrow(() -> ApiException.notFound("VISIT_NOT_FOUND", "OPD visit " + id + " not found"));
    }

    public PageResponse<OpdVisitResponse> listByPatient(UUID patientId, Pageable pageable) {
        return PageResponse.of(visitRepository.findByPatientId(patientId, pageable)
                .map(OpdVisitResponse::from));
    }

    public PageResponse<OpdVisitResponse> listByDate(LocalDate date, Pageable pageable) {
        LocalDate target = date != null ? date : LocalDate.now();
        return PageResponse.of(visitRepository.findByVisitDate(target, pageable)
                .map(OpdVisitResponse::from));
    }

    @Transactional
    public OpdVisitResponse updateVisit(UUID id, OpdVisitUpdateRequest req) {
        OpdVisit visit = findOrThrow(id);

        if (req.department()    != null) visit.setDepartment(req.department());
        if (req.doctorName()    != null) visit.setDoctorName(req.doctorName());
        if (req.symptoms()      != null) visit.setSymptoms(req.symptoms());
        if (req.diagnosis()     != null) visit.setDiagnosis(req.diagnosis());
        if (req.notes()         != null) visit.setNotes(req.notes());
        if (req.discount()      != null) visit.setDiscount(req.discount());
        if (req.paymentStatus() != null) visit.setPaymentStatus(req.paymentStatus());
        if (req.visitStatus()   != null) visit.setVisitStatus(req.visitStatus());

        visit.recalculateTotals();
        return OpdVisitResponse.from(visitRepository.save(visit));
    }

    @Transactional
    public void cancelVisit(UUID id) {
        OpdVisit visit = findOrThrow(id);
        if (visit.getVisitStatus() == VisitStatus.COMPLETED) {
            throw ApiException.badRequest("VISIT_COMPLETED", "Cannot cancel a completed visit");
        }
        visit.setVisitStatus(VisitStatus.CANCELLED);
        visitRepository.save(visit);
    }

    // ── Prescription ────────────────────────────────────────────────────────────

    @Transactional
    public PrescriptionResponse savePrescription(UUID visitId, PrescriptionRequest req) {
        OpdVisit visit = findOrThrow(visitId);

        Prescription prescription = prescriptionRepository.findByVisitId(visitId)
                .orElseGet(() -> {
                    Prescription p = new Prescription(visit);
                    visit.setPrescription(p);
                    return p;
                });

        prescription.setAdvice(req.advice());
        prescription.setFollowUpDays(req.followUpDays());

        // Replace all items
        prescription.getItems().clear();
        for (int i = 0; i < req.items().size(); i++) {
            PrescriptionItemRequest ir = req.items().get(i);
            PrescriptionItem item = new PrescriptionItem(
                    ir.medicineName(), ir.dose(), ir.frequency(), ir.duration(), ir.instructions());
            item.setSortOrder(i);
            prescription.addItem(item);
        }

        // Mark visit in progress if still registered
        if (visit.getVisitStatus() == VisitStatus.REGISTERED) {
            visit.setVisitStatus(VisitStatus.IN_PROGRESS);
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return PrescriptionResponse.from(saved);
    }

    public PrescriptionResponse getPrescription(UUID visitId) {
        return prescriptionRepository.findByVisitId(visitId)
                .map(PrescriptionResponse::from)
                .orElseThrow(() -> ApiException.notFound("PRESCRIPTION_NOT_FOUND",
                        "No prescription found for visit " + visitId));
    }

    // ── Bill (summary) ──────────────────────────────────────────────────────────

    public OpdVisitResponse getBill(UUID visitId) {
        return OpdVisitResponse.from(findOrThrow(visitId));
    }

    // ── Dashboard ───────────────────────────────────────────────────────────────

    public List<Object[]> getDailySummary(LocalDate date) {
        return visitRepository.countByStatusForDate(date != null ? date : LocalDate.now());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private OpdVisit findOrThrow(UUID id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("VISIT_NOT_FOUND", "OPD visit " + id + " not found"));
    }

    private String generateVisitNumber(LocalDate date) {
        int year = date.getYear();
        long seq  = visitRepository.nextSequenceForYear(year);
        return String.format("OPD-%d-%05d", year, seq);
    }
}
