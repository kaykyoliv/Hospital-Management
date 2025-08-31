package com.kayky.domain.operation;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.OperationUtils;
import com.kayky.commons.PatientUtils;
import com.kayky.domain.user.UserValidator;
import com.kayky.exception.ResourceNotFoundException;
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
    @DisplayName("findById: Should return OperationBaseResponse when the operation exists")
    void findById_ShouldReturnOperationBaseResponse_WhenOperationExists(){
        var savedOperation = OperationUtils.savedOperation();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedOperation));

        var response = service.findById(EXISTING_ID);

        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        Assertions.assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("findById: Should throw ResourceNotFoundException when the Operation does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenOperationDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(OPERATION_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll: Should return PageResponse when doctors exist")
    void findAll_ShouldReturnPageResponse_WhenOperationExists() {
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
    @DisplayName("save: Should return OperationBaseResponse when correct data")
    void save_ShouldReturnOperationBaseResponse_WhenCorrectData() {
        var request = OperationUtils.asBaseRequest();

        var savedOperation = OperationUtils.savedOperation();
        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        doNothing().when(userValidator).assertIfUserExist(request.getPatient().getId(), "Patient");
        doNothing().when(userValidator).assertIfUserExist(request.getDoctor().getId(), "Doctor");

        when(repository.save(any(Operation.class))).thenReturn(savedOperation);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).save(any(Operation.class));
    }

    @ParameterizedTest(name = "save: should throw exception when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void save_ShouldThrowResourceNotFoundException_WhenNonExistingUser(String nonExistingType){

        var request = OperationUtils.asBaseRequest();

        var nonExistingId = nonExistingType.equals("Doctor") ?
                request.getDoctor().getId() : request.getPatient().getId();
        var nonExistingName = nonExistingType.equals("Doctor") ?
                request.getDoctor().getFirstName() : request.getPatient().getFirstName();

        var existingId = nonExistingType.equals("Doctor") ?
                request.getPatient().getId() : request.getDoctor().getId();
        var existingType = nonExistingType.equals("Doctor") ? "Patient" : "Doctor";

        lenient().doNothing().when(userValidator).assertIfUserExist(existingId, existingType);

        doThrow(new ResourceNotFoundException(
                USER_NOT_FOUND_SAVE_OPERATION.formatted(nonExistingName, nonExistingId)))
                .when(userValidator).assertIfUserExist(nonExistingId, nonExistingType);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(USER_NOT_FOUND_SAVE_OPERATION.formatted(nonExistingName, nonExistingId));

        verify(userValidator).assertIfUserExist(nonExistingId, nonExistingType);
    }

    private static Stream<String> provideNonExistingTypes() {
        return Stream.of("Doctor", "Patient");
    }

    @Test
    @DisplayName("update: Should return OperationBaseResponse when update is valid")
    void update_ShouldReturnOperationBaseResponse_WhenUpdateIsValid() {
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

        verify(userValidator).getPatientIfExists(EXISTING_ID);
        verify(userValidator).getDoctorIfExists(EXISTING_ID);
        verify(repository).save(any(Operation.class));
    }


}