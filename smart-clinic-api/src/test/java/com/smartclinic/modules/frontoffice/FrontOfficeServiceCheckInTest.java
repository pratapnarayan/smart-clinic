package com.smartclinic.modules.frontoffice;

import com.smartclinic.core.exception.ApiException;
import com.smartclinic.modules.frontoffice.domain.Appointment;
import com.smartclinic.modules.frontoffice.domain.Appointment.AppointmentStatus;
import com.smartclinic.modules.frontoffice.dto.CheckInRequest;
import com.smartclinic.modules.frontoffice.repository.AppointmentRepository;
import com.smartclinic.modules.frontoffice.repository.OpdTokenRepository;
import com.smartclinic.modules.frontoffice.service.FrontOfficeService;
import com.smartclinic.modules.opd.dto.OpdVisitCreateRequest;
import com.smartclinic.modules.opd.dto.OpdVisitResponse;
import com.smartclinic.modules.opd.domain.OpdVisit;
import com.smartclinic.modules.opd.service.OpdService;
import com.smartclinic.modules.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FrontOfficeServiceCheckInTest {

    @Mock AppointmentRepository appointmentRepository;
    @Mock OpdTokenRepository    tokenRepository;
    @Mock PatientRepository     patientRepository;
    @Mock OpdService            opdService;
    @InjectMocks FrontOfficeService service;

    @Test
    void checkIn_throwsNotFound_whenAppointmentDoesNotExist() {
        UUID id = UUID.randomUUID();
        given(appointmentRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.checkIn(id, null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");

        verify(opdService, never()).createVisit(any());
    }

    @Test
    void checkIn_throwsAlreadyCheckedIn_whenStatusIsCheckedIn() {
        UUID id = UUID.randomUUID();
        Appointment apt = buildAppointment(id, AppointmentStatus.CHECKED_IN);
        given(appointmentRepository.findById(id)).willReturn(Optional.of(apt));

        assertThatThrownBy(() -> service.checkIn(id, null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("ALREADY_CHECKED_IN");

        verify(opdService, never()).createVisit(any());
    }

    @Test
    void checkIn_throwsAppointmentClosed_whenStatusIsCancelled() {
        UUID id = UUID.randomUUID();
        Appointment apt = buildAppointment(id, AppointmentStatus.CANCELLED);
        given(appointmentRepository.findById(id)).willReturn(Optional.of(apt));

        assertThatThrownBy(() -> service.checkIn(id, null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("APPOINTMENT_CLOSED");

        verify(opdService, never()).createVisit(any());
    }

    @Test
    void checkIn_throwsAppointmentClosed_whenStatusIsCompleted() {
        UUID id = UUID.randomUUID();
        Appointment apt = buildAppointment(id, AppointmentStatus.COMPLETED);
        given(appointmentRepository.findById(id)).willReturn(Optional.of(apt));

        assertThatThrownBy(() -> service.checkIn(id, null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("APPOINTMENT_CLOSED");

        verify(opdService, never()).createVisit(any());
    }

    @Test
    void checkIn_throwsAppointmentClosed_whenStatusIsNoShow() {
        UUID id = UUID.randomUUID();
        Appointment apt = buildAppointment(id, AppointmentStatus.NO_SHOW);
        given(appointmentRepository.findById(id)).willReturn(Optional.of(apt));

        assertThatThrownBy(() -> service.checkIn(id, null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("APPOINTMENT_CLOSED");

        verify(opdService, never()).createVisit(any());
    }

    @Test
    void checkIn_updatesStatusAndCreatesVisit_forConfirmedAppointment() {
        UUID id = UUID.randomUUID();
        Appointment apt = buildAppointment(id, AppointmentStatus.CONFIRMED);
        given(appointmentRepository.findById(id)).willReturn(Optional.of(apt));
        given(appointmentRepository.save(apt)).willReturn(apt);

        OpdVisitResponse mockVisit = buildMockVisitResponse(id);
        given(opdService.createVisit(any(OpdVisitCreateRequest.class))).willReturn(mockVisit);

        OpdVisitResponse result = service.checkIn(id, null);

        assertThat(apt.getStatus()).isEqualTo(AppointmentStatus.CHECKED_IN);
        verify(appointmentRepository).save(apt);

        ArgumentCaptor<OpdVisitCreateRequest> captor = ArgumentCaptor.forClass(OpdVisitCreateRequest.class);
        verify(opdService).createVisit(captor.capture());
        OpdVisitCreateRequest req = captor.getValue();

        assertThat(req.appointmentId()).isEqualTo(id);
        assertThat(req.visitSource()).isEqualTo(OpdVisit.VisitSource.APPOINTMENT);
        assertThat(req.patientId()).isEqualTo(apt.getPatientId());
        assertThat(result).isEqualTo(mockVisit);
    }

    @Test
    void checkIn_acceptsOptionalSymptoms_fromCheckInRequest() {
        UUID id = UUID.randomUUID();
        Appointment apt = buildAppointment(id, AppointmentStatus.SCHEDULED);
        given(appointmentRepository.findById(id)).willReturn(Optional.of(apt));
        given(appointmentRepository.save(apt)).willReturn(apt);
        given(opdService.createVisit(any())).willReturn(buildMockVisitResponse(id));

        CheckInRequest req = new CheckInRequest("Fever and cough", new BigDecimal("500.00"));
        service.checkIn(id, req);

        ArgumentCaptor<OpdVisitCreateRequest> captor = ArgumentCaptor.forClass(OpdVisitCreateRequest.class);
        verify(opdService).createVisit(captor.capture());
        assertThat(captor.getValue().symptoms()).isEqualTo("Fever and cough");
        assertThat(captor.getValue().consultationFee()).isEqualByComparingTo("500.00");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private Appointment buildAppointment(UUID id, AppointmentStatus status) {
        Appointment apt = Appointment.builder()
                .appointmentNumber("APT-2026-00001")
                .patientId(UUID.randomUUID())
                .patientName("Test Patient")
                .patientMobile("9999999999")
                .appointmentDate(LocalDate.now())
                .build();
        // id is declared on Appointment itself
        try {
            var idField = apt.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(apt, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        apt.setStatus(status);
        return apt;
    }

    private OpdVisitResponse buildMockVisitResponse(UUID appointmentId) {
        return new OpdVisitResponse(
                UUID.randomUUID(), "OPD-2026-00001",
                UUID.randomUUID(), "Test Patient",
                LocalDate.now(), null,
                null, null, null, null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                OpdVisit.PaymentStatus.PENDING, OpdVisit.VisitStatus.REGISTERED,
                OpdVisit.VisitSource.APPOINTMENT, appointmentId,
                java.util.List.of(), null,
                java.time.Instant.now()
        );
    }
}
