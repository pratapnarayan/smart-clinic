package com.smartclinic.modules.patient;

import com.smartclinic.core.exception.ApiException;
import com.smartclinic.modules.patient.domain.Patient;
import com.smartclinic.modules.patient.dto.PatientCreateRequest;
import com.smartclinic.modules.patient.repository.PatientRepository;
import com.smartclinic.modules.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock PatientRepository patientRepository;
    @InjectMocks PatientService patientService;

    @Test
    void findById_throwsNotFound_whenPatientDoesNotExist() {
        UUID id = UUID.randomUUID();
        given(patientRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(id))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void create_throwsConflict_whenMobileAlreadyExists() {
        given(patientRepository.existsByMobile("9876543210")).willReturn(true);

        PatientCreateRequest request = new PatientCreateRequest(
                "Raj", "Sharma", LocalDate.of(1990, 1, 1),
                Patient.Gender.MALE, "9876543210", null, null, null, null, null);

        assertThatThrownBy(() -> patientService.create(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void create_savesPatient_whenValid() {
        given(patientRepository.existsByMobile(any())).willReturn(false);
        given(patientRepository.save(any())).willAnswer(inv -> {
            Patient p = inv.getArgument(0);
            return p;
        });

        PatientCreateRequest request = new PatientCreateRequest(
                "Raj", "Sharma", LocalDate.of(1990, 1, 1),
                Patient.Gender.MALE, "9876543210", null, null, null, null, null);

        patientService.create(request);
        verify(patientRepository).save(any(Patient.class));
    }
}
