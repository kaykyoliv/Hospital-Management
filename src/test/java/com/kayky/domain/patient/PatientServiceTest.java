package com.kayky.domain.patient;

import com.kayky.commons.PatientUtils;
import com.kayky.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private static final String PATIENT_NOT_FOUND = "Patient not found";
    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;

    private PatientService service;

    @Mock
    private PatientRepository repository;

    private final PatientMapper mapper = Mappers.getMapper(PatientMapper.class);

    @BeforeEach
    void setUp() {
        service = new PatientService(repository, mapper);
    }

    @Test
    void shouldReturnPatientGetResponse_whenPatientExists() {
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);

        var expectedResponse = PatientUtils.asGetResponse(savedPatient);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedPatient));

        var patientFound = service.findById(EXISTING_ID);

        assertThat(patientFound)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenPatientDoesNotExist() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PATIENT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }



}