package com.kayky.domain.patient;

import com.kayky.commons.PatientUtils;
import com.kayky.exception.EmailAlreadyExistsException;
import com.kayky.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private static final String PATIENT_NOT_FOUND = "Patient not found";
    private static final String EMAIL_ALREADY_EXIST = "Email %s already in use";
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

    @Test
    void shouldReturnPatientPageResponse_whenPatientsExist(){
        PageRequest pageRequest = PageRequest.of(0, 3);
        var patientList = PatientUtils.newPatientList();
        var pagedPatient = new PageImpl<>(patientList, pageRequest, patientList.size());

        when(repository.findAll(pageRequest)).thenReturn(pagedPatient);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedPatient.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedPatient.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedPatient.getNumber());

        var expectedResponse = patientList.stream()
                        .map(mapper::toPatientGetResponse).toList();

        assertThat(result.getPatients())
                .usingRecursiveComparison()
                        .isEqualTo(expectedResponse);

        verify(repository).findAll(pageRequest);
    }

    @Test
    void shouldSavePatientAndReturnPostResponse_whenEmailIsUnique(){
        var request = PatientUtils.asPostRequest();
        var email = request.getEmail();

        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var expectedResponse = PatientUtils.asPostResponse(savedPatient);

        when(repository.findByEmail(email)).thenReturn(Optional.empty());
        when(repository.save(any(Patient.class))).thenReturn(savedPatient);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findByEmail(email);
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsException_whenEmailAlreadyExists(){
        var request = PatientUtils.asPostRequest();

        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var email = savedPatient.getEmail();

        request.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(savedPatient));

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXIST.formatted(email));

        verify(repository, times(0)).save(any());
        verify(repository).findByEmail(email);
    }

    @Test
    void shouldUpdatePatientAndReturnPutResponse_whenValidUpdate(){
        var putRequest = PatientUtils.asPutRequest();
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var updatedPatient = PatientUtils.updatedPatient();

        var expectedResponse = PatientUtils.asPutResponse(updatedPatient);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedPatient));
        when(repository.findByEmailAndIdNot(putRequest.getEmail(), EXISTING_ID)).thenReturn(Optional.empty());
        when(repository.save(any(Patient.class))).thenReturn(updatedPatient);

        var result = service.update(putRequest, EXISTING_ID);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
        verify(repository).findByEmailAndIdNot(putRequest.getEmail(), EXISTING_ID);
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldThrowResourceNotFoundException_whenPatientToUpdateDoesNotExist(){
        var request = PatientUtils.asPutRequest();

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request, NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PATIENT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    void shouldThrowEmailAlreadyExistsException_whenUpdatingToEmailAlreadyUsedByAnother(){
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var email = savedPatient.getEmail();
        var request = PatientUtils.asPutRequest();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedPatient));
        when(repository.findByEmailAndIdNot(email, EXISTING_ID)).thenReturn(Optional.of(savedPatient));

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXIST.formatted(email));

        verify(repository).findByEmailAndIdNot(email, EXISTING_ID);
    }




}