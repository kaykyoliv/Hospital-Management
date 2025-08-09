package com.kayky.domain.patient;

import com.kayky.commons.PatientUtils;
import com.kayky.domain.user.UserValidator;
import com.kayky.exception.EmailAlreadyExistsException;
import com.kayky.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientServiceTest")
class PatientServiceTest {

    private PatientService service;

    @Mock
    private PatientRepository repository;

    private final PatientMapper mapper = Mappers.getMapper(PatientMapper.class);

    @Mock
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        service = new PatientService(repository, mapper, userValidator);
    }

    @Test
    @DisplayName("findById: Should return PatientGetResponse when the patient exists")
    void findById_ShouldReturnPatientGetResponse_WhenPatientExists() {
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
    @DisplayName("findById: Should throw ResourceNotFoundException when the patient does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenPatientDoesNotExist() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PATIENT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll: Should return PatientPageResponse when patients exist")
    void findAll_ShouldReturnPatientPageResponse_WhenPatientsExist() {
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
    @DisplayName("save: Should return PostResponse when email is unique")
    void save_ShouldReturnPostResponse_WhenEmailIsUnique() {
        var request = PatientUtils.asPostRequest();
        var email = request.getEmail();

        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var expectedResponse = PatientUtils.asPostResponse(savedPatient);

        when(repository.save(any(Patient.class))).thenReturn(savedPatient);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).save(any(Patient.class));
    }

    @Test
    @DisplayName("save: Should throw EmailAlreadyExistsException when email is already in use")
    void save_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() {
        var request = PatientUtils.asPostRequest();

        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var email = savedPatient.getEmail();

        request.setEmail(email);

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXIST.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(email);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXIST.formatted(email));

        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("update: Should return PutResponse when update is valid")
    void update_ShouldReturnPutResponse_WhenUpdateIsValid() {
        var putRequest = PatientUtils.asPutRequest();
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var updatedPatient = PatientUtils.updatedPatient();

        var expectedResponse = PatientUtils.asPutResponse(updatedPatient);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedPatient));
        when(repository.save(any(Patient.class))).thenReturn(updatedPatient);

        var result = service.update(putRequest, EXISTING_ID);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
        verify(repository).save(any(Patient.class));
    }

    @Test
    @DisplayName("update: Should throw ResourceNotFoundException when patient does not exist")
    void update_ShouldThrowResourceNotFoundException_WhenPatientDoesNotExist() {
        var request = PatientUtils.asPutRequest();

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request, NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PATIENT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("update: Should throw EmailAlreadyExistsException when email is used by another patient")
    void update_ShouldThrowEmailAlreadyExistsException_WhenEmailUsedByAnotherPatient() {
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var email = savedPatient.getEmail();
        var request = PatientUtils.asPutRequest();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedPatient));

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXIST.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(request.getEmail(), EXISTING_ID);

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXIST.formatted(email));

        verify(userValidator).assertEmailDoesNotExist(request.getEmail(), EXISTING_ID);
    }
}