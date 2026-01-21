package com.kayky.domain.operation;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.OperationUtils;
import com.kayky.commons.PatientUtils;
import com.kayky.domain.user.UserValidator;
import com.kayky.core.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.stream.Stream;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Operation Service - Unit Tests")
@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    private OperationService service;

    @Mock
    private OperationRepository repository;

    @Mock
    private  UserValidator userValidator;

    private final OperationMapper mapper = Mappers.getMapper(OperationMapper.class);

    @BeforeEach
    void setUp(){
        service = new OperationService(repository, userValidator, mapper);
    }

    @Test
    @DisplayName("findById - Should return OperationBaseResponse when doctor exists")
    void findById_shouldReturnBaseResponse_whenOperationExists() {
        var savedOperation = OperationUtils.savedOperation();
        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedOperation));

        var result = service.findById(EXISTING_ID);

        Assertions.assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("findById - Should throw not-found exception when operation does not exist")
    void findById_shouldThrowNotFound_whenOperationDoesNotExist() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(OPERATION_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll - Should return paged response when operations exist")
    void findAll_shouldReturnPagedResponse_whenOperationsExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var pagedOperation = OperationUtils.operationProjectionPage();

        when(repository.findAllProjected(pageRequest)).thenReturn(pagedOperation);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedOperation.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedOperation.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedOperation.getNumber());

        var expectedResponse = OperationUtils.operationDetailsResponseList();

        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save - Should return base response when request is valid")
    void save_shouldReturnBaseResponse_whenCreatingValidOperation() {
        var request = OperationUtils.asBaseRequest();
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        var doctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var savedOperation = OperationUtils.savedOperation();
        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        doNothing().when(userValidator).assertIfUserExist(request.getPatientId(), "Patient");
        doNothing().when(userValidator).assertIfUserExist(request.getDoctorId(), "Doctor");

        when(userValidator.getDoctorIfExists(EXISTING_ID)).thenReturn(doctor);
        when(userValidator.getPatientIfExists(EXISTING_ID)).thenReturn(patient);

        when(repository.save(any(Operation.class))).thenReturn(savedOperation);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).save(any(Operation.class));
        verify(userValidator).assertIfUserExist(request.getPatientId(), "Patient");
        verify(userValidator).assertIfUserExist(request.getDoctorId(), "Doctor");
        verify(userValidator).getPatientIfExists(request.getPatientId());
        verify(userValidator).getDoctorIfExists(request.getDoctorId());
    }

    @Test
    @DisplayName("save - should throw not-found when patient does not exist")
    void save_shouldThrowNotFound_whenPatientDoesNotExist() {
        var request = OperationUtils.asBaseRequest();

        doThrow(new ResourceNotFoundException("Patient not found"))
                .when(userValidator).assertIfUserExist(request.getPatientId(), "Patient");

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");

        verify(userValidator).assertIfUserExist(request.getPatientId(), "Patient");
    }

    @Test
    @DisplayName("save - should throw not-found when doctor does not exist")
    void save_shouldThrowNotFound_whenDoctorDoesNotExist() {
        var request = OperationUtils.asBaseRequest();

        doThrow(new ResourceNotFoundException("Doctor not found"))
                .when(userValidator).assertIfUserExist(request.getDoctorId(), "Doctor");

        doNothing().when(userValidator).assertIfUserExist(request.getPatientId(), "Patient");

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor");

        verify(userValidator).assertIfUserExist(request.getPatientId(), "Patient");
        verify(userValidator).assertIfUserExist(request.getDoctorId(), "Doctor");
    }

    @Test
    @DisplayName("update - Should return base response when request is valid")
    void update_shouldReturnBaseResponse_whenUpdatingValidOperation() {
        var request = OperationUtils.asBaseRequest();
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        var doctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var savedOperation = OperationUtils.savedOperation();

        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedOperation));
        when(userValidator.getDoctorIfExists(EXISTING_ID)).thenReturn(doctor);
        when(userValidator.getPatientIfExists(EXISTING_ID)).thenReturn(patient);

        when(repository.save(any(Operation.class))).thenReturn(savedOperation);

        var result = service.update(request, EXISTING_ID);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(userValidator).getPatientIfExists(request.getPatientId());
        verify(userValidator).getDoctorIfExists(request.getDoctorId());
    }

    @Test
    @DisplayName("update - should throw not-found when patient does not exist")
    void update_shouldThrowNotFound_whenPatientDoesNotExist() {
        var request = OperationUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedOperation));

        when(userValidator.getPatientIfExists(request.getPatientId()))
                .thenThrow(new ResourceNotFoundException(
                        "Patient with id %d not found".formatted(request.getPatientId())));

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");

        verify(repository).findById(EXISTING_ID);
        verify(userValidator).getPatientIfExists(request.getPatientId());
    }

    @Test
    @DisplayName("update - should throw not-found when doctor does not exist")
    void update_shouldThrowNotFound_whenDoctorDoesNotExist() {
        var request = OperationUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedOperation));

        when(userValidator.getPatientIfExists(request.getPatientId()))
                .thenReturn(PatientUtils.savedPatient(request.getPatientId()));

        when(userValidator.getDoctorIfExists(request.getDoctorId()))
                .thenThrow(new ResourceNotFoundException(
                        "Doctor with id %d not found".formatted(request.getDoctorId())));

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor");

        verify(repository).findById(EXISTING_ID);
        verify(userValidator).getPatientIfExists(request.getPatientId());
        verify(userValidator).getDoctorIfExists(request.getDoctorId());
    }

    @Test
    @DisplayName("delete - Should remove operation when ID exists")
    void delete_ShouldRemoveOperation_WhenSuccessful() {
        when(repository.existsById(EXISTING_ID)).thenReturn(true);
        doNothing().when(repository).deleteById(EXISTING_ID);

        service.delete(EXISTING_ID);

        verify(repository).existsById(EXISTING_ID);
        verify(repository).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("delete - Should throw not-found exception when operation does not exist")
    void delete_shouldThrowNotFound_WhenIdDoesNotExists() {
        when(repository.existsById(NON_EXISTING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(OPERATION_NOT_FOUND);
    }
}