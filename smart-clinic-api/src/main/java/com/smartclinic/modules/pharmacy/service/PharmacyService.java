package com.smartclinic.modules.pharmacy.service;

import com.smartclinic.core.exception.ApiException;
import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.patient.repository.PatientRepository;
import com.smartclinic.modules.pharmacy.domain.*;
import com.smartclinic.modules.pharmacy.dto.*;
import com.smartclinic.modules.pharmacy.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PharmacyService {

    private static final Logger log = LoggerFactory.getLogger(PharmacyService.class);
    private static final DateTimeFormatter BILL_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final MedicineCategoryRepository categoryRepo;
    private final MedicineRepository          medicineRepo;
    private final MedicineBatchRepository     batchRepo;
    private final PharmacyBillRepository      billRepo;
    private final PatientRepository           patientRepo;   // cross-module UUID lookup only

    public PharmacyService(MedicineCategoryRepository categoryRepo,
                           MedicineRepository          medicineRepo,
                           MedicineBatchRepository     batchRepo,
                           PharmacyBillRepository      billRepo,
                           PatientRepository           patientRepo) {
        this.categoryRepo = categoryRepo;
        this.medicineRepo = medicineRepo;
        this.batchRepo    = batchRepo;
        this.billRepo     = billRepo;
        this.patientRepo  = patientRepo;
    }

    // ── Categories ──────────────────────────────────────────────────────────────

    public List<MedicineCategoryResponse> listCategories() {
        return categoryRepo.findAll().stream()
                .map(MedicineCategoryResponse::from).toList();
    }

    @Transactional
    public MedicineCategoryResponse createCategory(MedicineCategoryRequest req) {
        if (categoryRepo.existsByNameIgnoreCase(req.name())) {
            throw ApiException.conflict("DUPLICATE_CATEGORY",
                    "Category '" + req.name() + "' already exists");
        }
        return MedicineCategoryResponse.from(categoryRepo.save(new MedicineCategory(req.name())));
    }

    @Transactional
    public MedicineCategoryResponse updateCategory(UUID id, MedicineCategoryRequest req) {
        MedicineCategory cat = categoryRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("CATEGORY_NOT_FOUND", "Category " + id + " not found"));
        cat.setName(req.name());
        return MedicineCategoryResponse.from(categoryRepo.save(cat));
    }

    // ── Medicines ───────────────────────────────────────────────────────────────

    public PageResponse<MedicineResponse> searchMedicines(String query, UUID categoryId, Pageable pageable) {
        if (StringUtils.hasText(query)) {
            return PageResponse.of(medicineRepo.searchByName(query, pageable)
                    .map(m -> MedicineResponse.from(m, totalStock(m.getId()))));
        }
        if (categoryId != null) {
            return PageResponse.of(medicineRepo.findByCategoryId(categoryId, pageable)
                    .map(m -> MedicineResponse.from(m, totalStock(m.getId()))));
        }
        return PageResponse.of(medicineRepo.findAll(pageable)
                .map(m -> MedicineResponse.from(m, totalStock(m.getId()))));
    }

    public MedicineResponse getMedicine(UUID id) {
        Medicine m = findMedicineOrThrow(id);
        return MedicineResponse.from(m, totalStock(id));
    }

    @Transactional
    public MedicineResponse createMedicine(MedicineCreateRequest req) {
        MedicineCategory cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> ApiException.notFound("CATEGORY_NOT_FOUND",
                        "Category " + req.categoryId() + " not found"));
        if (medicineRepo.existsByNameIgnoreCaseAndCategoryId(req.name(), req.categoryId())) {
            throw ApiException.conflict("DUPLICATE_MEDICINE",
                    "'" + req.name() + "' already exists in this category");
        }
        Medicine medicine = Medicine.builder()
                .category(cat)
                .name(req.name())
                .genericName(req.genericName())
                .unit(req.unit())
                .reorderLevel(req.reorderLevel())
                .build();
        return MedicineResponse.from(medicineRepo.save(medicine), 0);
    }

    @Transactional
    public MedicineResponse updateMedicine(UUID id, MedicineCreateRequest req) {
        Medicine m   = findMedicineOrThrow(id);
        MedicineCategory cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> ApiException.notFound("CATEGORY_NOT_FOUND",
                        "Category " + req.categoryId() + " not found"));
        m.setCategory(cat);
        m.setName(req.name());
        m.setGenericName(req.genericName());
        m.setUnit(req.unit());
        m.setReorderLevel(req.reorderLevel());
        return MedicineResponse.from(medicineRepo.save(m), totalStock(id));
    }

    @Transactional
    public void deleteMedicine(UUID id) {
        Medicine m = findMedicineOrThrow(id);
        medicineRepo.delete(m);   // soft-delete via @SQLDelete
    }

    public List<MedicineResponse> getLowStockMedicines() {
        return medicineRepo.findLowStockMedicines().stream()
                .map(m -> MedicineResponse.from(m, totalStock(m.getId()))).toList();
    }

    // ── Batches (Stock In) ───────────────────────────────────────────────────────

    public List<MedicineBatchResponse> getBatchesForMedicine(UUID medicineId) {
        findMedicineOrThrow(medicineId);
        return batchRepo.findByMedicineIdOrderByExpiryDateAsc(medicineId).stream()
                .map(MedicineBatchResponse::from).toList();
    }

    public StockSummaryResponse getStockSummary(UUID medicineId) {
        Medicine m   = findMedicineOrThrow(medicineId);
        List<MedicineBatch> batches = batchRepo.findByMedicineIdOrderByExpiryDateAsc(medicineId);
        int totalStock = batches.stream().mapToInt(MedicineBatch::getQuantity).sum();
        BigDecimal lowestPrice = batches.stream()
                .map(MedicineBatch::getSalePrice)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        LocalDate nearestExpiry = batches.stream()
                .map(MedicineBatch::getExpiryDate)
                .min(LocalDate::compareTo).orElse(null);
        return new StockSummaryResponse(
                m.getId(), m.getName(), m.getGenericName(), m.getUnit(),
                totalStock, m.getReorderLevel(), totalStock <= m.getReorderLevel(),
                lowestPrice, nearestExpiry,
                batches.stream().map(MedicineBatchResponse::from).toList()
        );
    }

    @Transactional
    public MedicineBatchResponse addBatch(MedicineBatchCreateRequest req) {
        Medicine medicine = findMedicineOrThrow(req.medicineId());
        if (batchRepo.existsByMedicineIdAndBatchNumber(req.medicineId(), req.batchNumber())) {
            throw ApiException.conflict("DUPLICATE_BATCH",
                    "Batch '" + req.batchNumber() + "' already exists for this medicine");
        }
        MedicineBatch batch = MedicineBatch.builder()
                .medicine(medicine)
                .batchNumber(req.batchNumber())
                .expiryDate(req.expiryDate())
                .quantity(req.quantity())
                .purchasePrice(req.purchasePrice())
                .salePrice(req.salePrice())
                .build();
        return MedicineBatchResponse.from(batchRepo.save(batch));
    }

    public List<MedicineBatchResponse> getExpiringBatches(int withinDays) {
        LocalDate cutoff = LocalDate.now().plusDays(withinDays);
        return batchRepo.findExpiringBefore(cutoff).stream()
                .map(MedicineBatchResponse::from).toList();
    }

    // ── Billing ─────────────────────────────────────────────────────────────────

    @Transactional
    public BillResponse createBill(BillCreateRequest req) {
        // Optional patient lookup (cross-module UUID only)
        String patientName = null;
        if (req.patientId() != null) {
            var patient = patientRepo.findById(req.patientId())
                    .orElseThrow(() -> ApiException.notFound("PATIENT_NOT_FOUND",
                            "Patient " + req.patientId() + " not found"));
            patientName = patient.getFirstName() + " " + patient.getLastName();
        }

        PharmacyBill bill = PharmacyBill.builder()
                .patientId(req.patientId())
                .patientName(patientName)
                .billNumber(generateBillNumber())
                .paymentMode(StringUtils.hasText(req.paymentMode()) ? req.paymentMode() : "CASH")
                .discount(req.discount() != null ? req.discount() : BigDecimal.ZERO)
                .build();

        for (BillItemRequest itemReq : req.items()) {
            MedicineBatch batch = batchRepo.findById(itemReq.batchId())
                    .orElseThrow(() -> ApiException.notFound("BATCH_NOT_FOUND",
                            "Batch " + itemReq.batchId() + " not found"));

            if (batch.isExpired()) {
                throw ApiException.badRequest("BATCH_EXPIRED",
                        "Batch " + batch.getBatchNumber() + " expired on " + batch.getExpiryDate());
            }

            // Deduct stock — throws IllegalStateException if insufficient
            try {
                batch.deductStock(itemReq.quantity());
            } catch (IllegalStateException e) {
                throw ApiException.badRequest("INSUFFICIENT_STOCK", e.getMessage());
            }
            batchRepo.save(batch);

            String medicineName = batch.getMedicine().getName();
            bill.addItem(new PharmacyBillItem(batch, medicineName, itemReq.quantity(), batch.getSalePrice()));

            // Low-stock warning after deduction
            if (batch.isLowStock(batch.getMedicine().getReorderLevel())) {
                log.warn("[Pharmacy] Low stock alert: {} (batch {}) — {} units remaining, reorder level {}",
                        medicineName, batch.getBatchNumber(),
                        batch.getQuantity(), batch.getMedicine().getReorderLevel());
            }
        }

        bill.recalculateTotals();
        PharmacyBill saved = billRepo.save(bill);
        log.info("Pharmacy bill {} created — net ₹{}", saved.getBillNumber(), saved.getNetAmount());
        return BillResponse.from(saved);
    }

    public BillResponse getBill(UUID id) {
        return billRepo.findById(id)
                .map(BillResponse::from)
                .orElseThrow(() -> ApiException.notFound("BILL_NOT_FOUND", "Bill " + id + " not found"));
    }

    public PageResponse<BillResponse> listBillsByPatient(UUID patientId, Pageable pageable) {
        return PageResponse.of(billRepo.findByPatientId(patientId, pageable).map(BillResponse::from));
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private Medicine findMedicineOrThrow(UUID id) {
        return medicineRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("MEDICINE_NOT_FOUND",
                        "Medicine " + id + " not found"));
    }

    private int totalStock(UUID medicineId) {
        return batchRepo.totalAvailableStock(medicineId, LocalDate.now());
    }

    /** Generates bill number: PH-20260530-001 */
    private String generateBillNumber() {
        long seq = billRepo.nextDailySequence();
        return "PH-" + LocalDate.now().format(BILL_DATE_FMT) + "-" + String.format("%03d", seq);
    }
}
