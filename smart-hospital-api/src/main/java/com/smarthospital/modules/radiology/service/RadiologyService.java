package com.smarthospital.modules.radiology.service;

import com.smarthospital.core.exception.ApiException;
import com.smarthospital.core.pagination.PageResponse;
import com.smarthospital.modules.radiology.domain.*;
import com.smarthospital.modules.radiology.domain.RadiologyOrder.OrderStatus;
import com.smarthospital.modules.radiology.domain.RadiologyOrderItem.ItemStatus;
import com.smarthospital.modules.radiology.dto.*;
import com.smarthospital.modules.radiology.repository.*;
import com.smarthospital.modules.patient.domain.Patient;
import com.smarthospital.modules.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RadiologyService {

    private static final Logger log = LoggerFactory.getLogger(RadiologyService.class);

    private final ImagingModalityRepository modalityRepository;
    private final ImagingStudyRepository    studyRepository;
    private final RadiologyOrderRepository  orderRepository;
    private final PatientRepository         patientRepository;

    public RadiologyService(ImagingModalityRepository modalityRepository,
                            ImagingStudyRepository    studyRepository,
                            RadiologyOrderRepository  orderRepository,
                            PatientRepository         patientRepository) {
        this.modalityRepository = modalityRepository;
        this.studyRepository    = studyRepository;
        this.orderRepository    = orderRepository;
        this.patientRepository  = patientRepository;
    }

    // ── Modalities ────────────────────────────────────────────────────────────

    public List<ModalityResponse> listModalities() {
        return modalityRepository.findByActiveTrue().stream().map(ModalityResponse::from).toList();
    }

    @Transactional
    public ModalityResponse createModality(ModalityRequest req) {
        if (modalityRepository.existsByNameIgnoreCase(req.name()))
            throw ApiException.conflict("MODALITY_EXISTS", "Modality '" + req.name() + "' already exists");
        if (modalityRepository.existsByCodeIgnoreCase(req.code()))
            throw ApiException.conflict("MODALITY_CODE_EXISTS", "Code '" + req.code() + "' already in use");
        return ModalityResponse.from(modalityRepository.save(
                ImagingModality.builder()
                        .name(req.name()).code(req.code().toUpperCase())
                        .description(req.description()).build()));
    }

    // ── Studies ───────────────────────────────────────────────────────────────

    public List<StudyResponse> listStudies(UUID modalityId) {
        if (modalityId != null)
            return studyRepository.findByModalityIdAndActiveTrue(modalityId)
                    .stream().map(StudyResponse::from).toList();
        return studyRepository.findByActiveTrue().stream().map(StudyResponse::from).toList();
    }

    public StudyResponse getStudy(UUID id) {
        return studyRepository.findById(id)
                .map(StudyResponse::from)
                .orElseThrow(() -> ApiException.notFound("STUDY_NOT_FOUND", "Study " + id + " not found"));
    }

    @Transactional
    public StudyResponse createStudy(StudyRequest req) {
        if (studyRepository.existsByCodeIgnoreCase(req.code()))
            throw ApiException.conflict("STUDY_CODE_EXISTS", "Study code '" + req.code() + "' already exists");
        modalityRepository.findById(req.modalityId())
                .orElseThrow(() -> ApiException.notFound("MODALITY_NOT_FOUND", "Modality not found"));
        ImagingStudy study = ImagingStudy.builder()
                .code(req.code().toUpperCase())
                .name(req.name())
                .modalityId(req.modalityId())
                .description(req.description())
                .price(req.price() != null ? req.price() : java.math.BigDecimal.ZERO)
                .prepInstructions(req.prepInstructions())
                .build();
        return StudyResponse.from(studyRepository.save(study));
    }

    @Transactional
    public StudyResponse updateStudy(UUID id, StudyRequest req) {
        ImagingStudy study = studyRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("STUDY_NOT_FOUND", "Study " + id + " not found"));
        if (req.name()             != null) study.setName(req.name());
        if (req.modalityId()       != null) study.setModalityId(req.modalityId());
        if (req.description()      != null) study.setDescription(req.description());
        if (req.price()            != null) study.setPrice(req.price());
        if (req.prepInstructions() != null) study.setPrepInstructions(req.prepInstructions());
        return StudyResponse.from(studyRepository.save(study));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @Transactional
    public RadiologyOrderResponse createOrder(RadiologyOrderCreateRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> ApiException.notFound("PATIENT_NOT_FOUND",
                        "Patient " + req.patientId() + " not found"));

        List<ImagingStudy> studies = studyRepository.findAllById(req.studyIds());
        if (studies.size() != req.studyIds().size())
            throw ApiException.badRequest("STUDY_NOT_FOUND", "One or more study IDs are invalid");

        // Build a modality name map to snapshot with each item
        Map<UUID, String> modalityNames = modalityRepository.findAllById(
                        studies.stream().map(ImagingStudy::getModalityId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(ImagingModality::getId, ImagingModality::getName));

        RadiologyOrder order = RadiologyOrder.builder()
                .orderNumber(generateOrderNumber())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .patientMobile(patient.getMobile())
                .referredById(req.referredById())
                .referredByName(req.referredByName())
                .sourceType(req.sourceType() != null ? req.sourceType() : RadiologyOrder.SourceType.WALK_IN)
                .sourceId(req.sourceId())
                .priority(req.priority() != null ? req.priority() : RadiologyOrder.Priority.ROUTINE)
                .scheduledAt(req.scheduledAt())
                .clinicalHistory(req.clinicalHistory())
                .notes(req.notes())
                .build();

        studies.forEach(s -> order.getItems().add(
                new RadiologyOrderItem(order, s,
                        modalityNames.getOrDefault(s.getModalityId(), "Unknown"))));
        order.recalculateTotals();

        if (req.scheduledAt() != null) order.setStatus(OrderStatus.SCHEDULED);

        RadiologyOrder saved = orderRepository.save(order);
        log.info("Radiology order {} created for patient {} ({} studies)",
                saved.getOrderNumber(), patient.getId(), studies.size());
        return RadiologyOrderResponse.from(saved);
    }

    public RadiologyOrderResponse getOrder(UUID id) {
        return RadiologyOrderResponse.from(findOrThrow(id));
    }

    public PageResponse<RadiologyOrderResponse> listOrders(OrderStatus status, Pageable pageable) {
        if (status != null)
            return PageResponse.of(orderRepository.findByStatus(status, pageable)
                    .map(RadiologyOrderResponse::from));
        return PageResponse.of(orderRepository.findAll(pageable).map(RadiologyOrderResponse::from));
    }

    public PageResponse<RadiologyOrderResponse> listByPatient(UUID patientId, Pageable pageable) {
        return PageResponse.of(orderRepository.findByPatientId(patientId, pageable)
                .map(RadiologyOrderResponse::from));
    }

    @Transactional
    public RadiologyOrderResponse scheduleOrder(UUID id, LocalDateTime scheduledAt) {
        RadiologyOrder order = findOrThrow(id);
        if (order.getStatus() != OrderStatus.PENDING)
            throw ApiException.badRequest("INVALID_STATUS", "Only PENDING orders can be scheduled");
        order.setScheduledAt(scheduledAt);
        order.setStatus(OrderStatus.SCHEDULED);
        return RadiologyOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public RadiologyOrderResponse startOrder(UUID id) {
        RadiologyOrder order = findOrThrow(id);
        if (order.getStatus() != OrderStatus.SCHEDULED && order.getStatus() != OrderStatus.PENDING)
            throw ApiException.badRequest("INVALID_STATUS", "Order must be PENDING or SCHEDULED to start");
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.getItems().stream()
                .filter(i -> i.getStatus() == ItemStatus.PENDING)
                .forEach(i -> i.setStatus(ItemStatus.IN_PROGRESS));
        return RadiologyOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public RadiologyOrderResponse enterReport(UUID orderId, UUID itemId, ReportEntryRequest req) {
        RadiologyOrder order = findOrThrow(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED)
            throw ApiException.badRequest("ORDER_CANCELLED", "Cannot report on a cancelled order");

        RadiologyOrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("ITEM_NOT_FOUND",
                        "Item " + itemId + " not found in order " + orderId));

        item.setFindings(req.findings());
        item.setImpression(req.impression());
        item.setReportedAt(LocalDateTime.now());
        item.setReportedBy(req.reportedBy());
        item.setStatus(ItemStatus.REPORTED);

        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.SCHEDULED)
            order.setStatus(OrderStatus.IN_PROGRESS);
        order.syncStatusFromItems();

        return RadiologyOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public RadiologyOrderResponse cancelOrder(UUID id) {
        RadiologyOrder order = findOrThrow(id);
        if (order.getStatus() == OrderStatus.COMPLETED)
            throw ApiException.badRequest("ORDER_COMPLETED", "Cannot cancel a completed order");
        order.setStatus(OrderStatus.CANCELLED);
        return RadiologyOrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public RadiologyOrderResponse updatePayment(UUID id, RadiologyOrder.PaymentStatus paymentStatus) {
        RadiologyOrder order = findOrThrow(id);
        order.setPaymentStatus(paymentStatus);
        return RadiologyOrderResponse.from(orderRepository.save(order));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    public RadiologyDashboardResponse getDashboard() {
        return new RadiologyDashboardResponse(
                orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.countByStatus(OrderStatus.SCHEDULED),
                orderRepository.countByStatus(OrderStatus.IN_PROGRESS),
                orderRepository.countByStatus(OrderStatus.COMPLETED),
                studyRepository.count()
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RadiologyOrder findOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND",
                        "Radiology order " + id + " not found"));
    }

    private String generateOrderNumber() {
        int year = LocalDate.now().getYear();
        long seq = orderRepository.nextSequenceForYear(year);
        return String.format("RAD-%d-%05d", year, seq);
    }
}
