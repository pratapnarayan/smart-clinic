package com.smartclinic.modules.pathology.service;

import com.smartclinic.core.exception.ApiException;
import com.smartclinic.core.pagination.PageResponse;
import com.smartclinic.modules.pathology.domain.*;
import com.smartclinic.modules.pathology.domain.LabOrder.OrderStatus;
import com.smartclinic.modules.pathology.domain.LabOrderItem.ItemStatus;
import com.smartclinic.modules.pathology.dto.*;
import com.smartclinic.modules.pathology.repository.*;
import com.smartclinic.modules.patient.domain.Patient;
import com.smartclinic.modules.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PathologyService {

    private static final Logger log = LoggerFactory.getLogger(PathologyService.class);

    private final LabTestCategoryRepository categoryRepository;
    private final LabTestRepository         testRepository;
    private final LabOrderRepository        orderRepository;
    private final PatientRepository         patientRepository;

    public PathologyService(LabTestCategoryRepository categoryRepository,
                            LabTestRepository         testRepository,
                            LabOrderRepository        orderRepository,
                            PatientRepository         patientRepository) {
        this.categoryRepository = categoryRepository;
        this.testRepository     = testRepository;
        this.orderRepository    = orderRepository;
        this.patientRepository  = patientRepository;
    }

    // ── Categories ────────────────────────────────────────────────────────────

    public List<LabCategoryResponse> listCategories() {
        return categoryRepository.findByActiveTrue().stream().map(LabCategoryResponse::from).toList();
    }

    @Transactional
    public LabCategoryResponse createCategory(LabCategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.name()))
            throw ApiException.conflict("CATEGORY_EXISTS", "Category '" + req.name() + "' already exists");
        return LabCategoryResponse.from(categoryRepository.save(
                LabTestCategory.builder().name(req.name()).description(req.description()).build()));
    }

    // ── Test catalog ──────────────────────────────────────────────────────────

    public List<LabTestResponse> listTests(UUID categoryId) {
        if (categoryId != null)
            return testRepository.findByCategoryIdAndActiveTrue(categoryId)
                    .stream().map(LabTestResponse::from).toList();
        return testRepository.findByActiveTrue().stream().map(LabTestResponse::from).toList();
    }

    public LabTestResponse getTest(UUID id) {
        return testRepository.findById(id)
                .map(LabTestResponse::from)
                .orElseThrow(() -> ApiException.notFound("TEST_NOT_FOUND", "Lab test " + id + " not found"));
    }

    @Transactional
    public LabTestResponse createTest(LabTestRequest req) {
        if (testRepository.existsByCodeIgnoreCase(req.code()))
            throw ApiException.conflict("TEST_CODE_EXISTS", "Test code '" + req.code() + "' already exists");
        categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> ApiException.notFound("CATEGORY_NOT_FOUND", "Category not found"));
        LabTest test = LabTest.builder()
                .code(req.code().toUpperCase())
                .name(req.name())
                .categoryId(req.categoryId())
                .description(req.description())
                .price(req.price() != null ? req.price() : java.math.BigDecimal.ZERO)
                .turnaroundHours(req.turnaroundHours() != null ? req.turnaroundHours() : 24)
                .unit(req.unit())
                .normalRange(req.normalRange())
                .build();
        return LabTestResponse.from(testRepository.save(test));
    }

    @Transactional
    public LabTestResponse updateTest(UUID id, LabTestRequest req) {
        LabTest test = testRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("TEST_NOT_FOUND", "Lab test " + id + " not found"));
        if (req.name()            != null) test.setName(req.name());
        if (req.categoryId()      != null) test.setCategoryId(req.categoryId());
        if (req.description()     != null) test.setDescription(req.description());
        if (req.price()           != null) test.setPrice(req.price());
        if (req.turnaroundHours() != null) test.setTurnaroundHours(req.turnaroundHours());
        if (req.unit()            != null) test.setUnit(req.unit());
        if (req.normalRange()     != null) test.setNormalRange(req.normalRange());
        return LabTestResponse.from(testRepository.save(test));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @Transactional
    public LabOrderResponse createOrder(LabOrderCreateRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> ApiException.notFound("PATIENT_NOT_FOUND",
                        "Patient " + req.patientId() + " not found"));

        List<LabTest> tests = testRepository.findAllById(req.testIds());
        if (tests.size() != req.testIds().size())
            throw ApiException.badRequest("TEST_NOT_FOUND", "One or more test IDs are invalid");

        LabOrder order = LabOrder.builder()
                .orderNumber(generateOrderNumber())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .patientMobile(patient.getMobile())
                .referredById(req.referredById())
                .referredByName(req.referredByName())
                .sourceType(req.sourceType() != null ? req.sourceType() : LabOrder.SourceType.WALK_IN)
                .sourceId(req.sourceId())
                .priority(req.priority() != null ? req.priority() : LabOrder.Priority.ROUTINE)
                .notes(req.notes())
                .build();

        tests.forEach(t -> order.getItems().add(new LabOrderItem(order, t)));
        order.recalculateTotals();

        LabOrder saved = orderRepository.save(order);
        log.info("Lab order {} created for patient {} ({} tests)",
                saved.getOrderNumber(), patient.getId(), tests.size());
        return LabOrderResponse.from(saved);
    }

    public LabOrderResponse getOrder(UUID id) {
        return LabOrderResponse.from(findOrThrow(id));
    }

    public PageResponse<LabOrderResponse> listOrders(OrderStatus status, Pageable pageable) {
        if (status != null)
            return PageResponse.of(orderRepository.findByStatus(status, pageable)
                    .map(LabOrderResponse::from));
        return PageResponse.of(orderRepository.findAll(pageable).map(LabOrderResponse::from));
    }

    public PageResponse<LabOrderResponse> listByPatient(UUID patientId, Pageable pageable) {
        return PageResponse.of(orderRepository.findByPatientId(patientId, pageable)
                .map(LabOrderResponse::from));
    }

    @Transactional
    public LabOrderResponse collectSample(UUID orderId) {
        LabOrder order = findOrThrow(orderId);
        if (order.getStatus() != OrderStatus.PENDING)
            throw ApiException.badRequest("INVALID_STATUS",
                    "Sample can only be collected for PENDING orders");
        order.setStatus(OrderStatus.SAMPLE_COLLECTED);
        order.setSampleCollectedAt(LocalDateTime.now());
        order.getItems().forEach(i -> i.setStatus(ItemStatus.IN_PROGRESS));
        return LabOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public LabOrderResponse startProcessing(UUID orderId) {
        LabOrder order = findOrThrow(orderId);
        if (order.getStatus() != OrderStatus.SAMPLE_COLLECTED)
            throw ApiException.badRequest("INVALID_STATUS",
                    "Order must be in SAMPLE_COLLECTED state to start processing");
        order.setStatus(OrderStatus.IN_PROGRESS);
        return LabOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public LabOrderResponse enterResult(UUID orderId, UUID itemId, ResultEntryRequest req) {
        LabOrder order = findOrThrow(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED)
            throw ApiException.badRequest("ORDER_CANCELLED", "Cannot enter results for a cancelled order");

        LabOrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("ITEM_NOT_FOUND",
                        "Order item " + itemId + " not found in order " + orderId));

        item.setResult(req.result());
        item.setResultNote(req.resultNote());
        item.setResultEnteredAt(LocalDateTime.now());
        item.setResultEnteredBy(req.enteredBy());
        item.setStatus(ItemStatus.COMPLETED);

        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.SAMPLE_COLLECTED) {
            order.setStatus(OrderStatus.IN_PROGRESS);
        }
        order.syncStatusFromItems();

        return LabOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public LabOrderResponse cancelOrder(UUID orderId) {
        LabOrder order = findOrThrow(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED)
            throw ApiException.badRequest("ORDER_COMPLETED", "Cannot cancel a completed order");
        order.setStatus(OrderStatus.CANCELLED);
        return LabOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public LabOrderResponse updatePayment(UUID orderId, LabOrder.PaymentStatus paymentStatus) {
        LabOrder order = findOrThrow(orderId);
        order.setPaymentStatus(paymentStatus);
        return LabOrderResponse.from(orderRepository.save(order));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    public PathologyDashboardResponse getDashboard() {
        return new PathologyDashboardResponse(
                orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.countByStatus(OrderStatus.SAMPLE_COLLECTED),
                orderRepository.countByStatus(OrderStatus.IN_PROGRESS),
                orderRepository.countByStatus(OrderStatus.COMPLETED),
                testRepository.count()
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private LabOrder findOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND",
                        "Lab order " + id + " not found"));
    }

    private String generateOrderNumber() {
        int year = java.time.LocalDate.now().getYear();
        long seq = orderRepository.nextSequenceForYear(year);
        return String.format("LAB-%d-%05d", year, seq);
    }
}
