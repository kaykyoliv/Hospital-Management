package com.kayky.domain.report;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.OperationUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.ReportUtils;
import com.kayky.core.exception.OperationMismatchException;
import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.validator.ReportValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.stream.Stream;

import static com.kayky.commons.ReportUtils.asBaseRequest;
import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Report Service - Unit Tests")
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private ReportService service;

    @Mock
    private ReportRepository repository;

    @Mock
    private ReportValidator reportValidator;

    @Mock
    private ReportMapper mapper;

    @BeforeEach
    void setUp() {
        service = new ReportService(repository, reportValidator, mapper);
    }

    private ReportValidator.ValidationResult mockValidatorResult(ReportBaseRequest request){
        var validationResult = ReportUtils.validationResult();
        when(reportValidator.validate(request)).thenReturn(validationResult);
        return validationResult;
    }

    private void mockNoExistingReport(ReportValidator.ValidationResult validatorResult) {
        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(false);
    }

    private void mockExistingReport(ReportValidator.ValidationResult validatorResult) {
        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(true);
    }

    private void mockValidatorWithException(ReportBaseRequest request, RuntimeException exception) {
        when(reportValidator.validate(request)).thenThrow(exception);
    }

    @Test
    @DisplayName("findById - Should return ReportBaseResponse when report exists")
    void findById_shouldReturnBaseResponse_whenReportExists() {
        var savedReport = ReportUtils.savedReport();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        when(mapper.toReportBaseResponse(savedReport))
                .thenReturn(ReportUtils.asBaseResponse(savedReport));


        var response = service.findById(EXISTING_ID);

        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("findById - Should throw not-found exception when report does not exist")
    void findById_shouldThrowNotFound_whenReportDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(REPORT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll - Should return paged response when reports exist")
    void findAll_shouldReturnPagedResponse_whenReportsExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var reportList = ReportUtils.reportList();
        var pagedReports = PageUtils.toPage(reportList);

        when(repository.findAll(pageRequest)).thenReturn(pagedReports);

        when(mapper.toReportBaseResponse(any(Report.class)))
                .thenAnswer(invocation -> ReportUtils.asBaseResponse(invocation.getArgument(0)));

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedReports.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedReports.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedReports.getNumber());

        var expectedResponse = ReportUtils.baseResponseList();

        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save - Should return base response when request is valid")
    void save_shouldReturnBaseResponse_whenCreatingValidReport() {
        var savedReport = ReportUtils.savedReport();

        var request = asBaseRequest();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        var validatorResult = mockValidatorResult(request);

        mockNoExistingReport(validatorResult);

        when(mapper.toEntity(request, validatorResult.patient(), validatorResult.doctor(), validatorResult.operation()))
                .thenReturn(savedReport);
        when(repository.save(savedReport)).thenReturn(savedReport);
        when(mapper.toReportBaseResponse(savedReport)).thenReturn(expectedResponse);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save: should throw report-already-exists exception when report already exists for the operation")
    void save_shouldThrowAlreadyExists_whenReportAlreadyExists() {
        var request = asBaseRequest();
        var validatorResult = mockValidatorResult(request);

        mockExistingReport(validatorResult);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ReportAlreadyExistsException.class)
                .hasMessage(REPORT_ALREADY_EXISTS.formatted(request.operationId()));
    }

    @ParameterizedTest(name = "save - should throw-not-found when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void save_shouldThrowNotFound_whenNonExistingType(String nonExistingType) {
        var request = asBaseRequest();

        when(reportValidator.validate(request))
                .thenThrow(new ResourceNotFoundException(nonExistingType + " not found"));

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingType);
    }

    @Test
    @DisplayName("save - should fail when operation patient does not match")
    void save_shouldThrowMismatch_whenOperationPatientDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        var exception = new OperationMismatchException(String.format(OPERATION_PATIENT_MISMATCH, savedOperation.getPatient().getId(), request.patientId()));

        mockValidatorWithException(request, exception);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation patient");

        verify(reportValidator).validate(request);
    }

    @Test
    @DisplayName("save - should fail when operation doctor does not match")
    void save_shouldThrowMismatch_whenOperationDoctorDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        var exception = new OperationMismatchException(String.format(OPERATION_DOCTOR_MISMATCH, savedOperation.getDoctor().getId(), request.doctorId()));

        mockValidatorWithException(request, exception);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation doctor");

        verify(reportValidator).validate(request);
    }

    @Test
    @DisplayName("update - Should return base response when request is valid")
    void update_shouldReturnBaseResponse_whenUpdatingValidReport() {
        var savedReport = ReportUtils.savedReport();

        var request = ReportUtils.asBaseRequest();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        var validatorResult = mockValidatorResult(request);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        mockNoExistingReport(validatorResult);

        when(repository.save(any(Report.class))).thenReturn(savedReport);

        when(mapper.toReportBaseResponse(savedReport)).thenReturn(expectedResponse);

        var result = service.update(request, EXISTING_ID);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(reportValidator).validate(request);
        verify(repository).save(any(Report.class));
        verify(mapper).toReportBaseResponse(savedReport);
    }

    @Test
    @DisplayName("update - Should throw not-found exception when report does not exist")
    void update_shouldThrowNotFound_whenUpdatingNonExistingReport() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(ReportUtils.asBaseRequest(), NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(REPORT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest(name = "update - should throw-not-found when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void update_shouldThrowNotFound_whenNonExistingType(String nonExistingType) {
        var request = ReportUtils.asBaseRequest();
        var savedReport = ReportUtils.savedReport();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        when(reportValidator.validate(request))
                .thenThrow(new ResourceNotFoundException(nonExistingType + " not found"));

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingType);

    }

    @Test
    @DisplayName("update: should throw report-already-exists exception when report already exists for the operation")
    void update_shouldThrowAlreadyExists_whenReportAlreadyExists() {
        var request = asBaseRequest();

        var savedReport = ReportUtils.savedReport();
        savedReport.getOperation().setId(NON_EXISTING_ID);

        var validatorResult = mockValidatorResult(request);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        mockExistingReport(validatorResult);

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(ReportAlreadyExistsException.class)
                .hasMessage(REPORT_ALREADY_EXISTS.formatted(request.operationId()));
    }

    @Test
    @DisplayName("update - should fail when operation patient does not match")
    void update_shouldThrowMismatch_whenOperationPatientDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        var savedReport = ReportUtils.savedReport();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        var exception = new OperationMismatchException(String.format(OPERATION_PATIENT_MISMATCH, savedOperation.getPatient().getId(), request.patientId()));

        mockValidatorWithException(request, exception);

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation patient");

        verify(reportValidator).validate(request);
    }

    @Test
    @DisplayName("update - should fail when operation doctor does not match")
    void update_shouldThrowMismatch_whenOperationDoctorDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        var savedReport = ReportUtils.savedReport();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        var exception = new OperationMismatchException(String.format(OPERATION_DOCTOR_MISMATCH, savedOperation.getDoctor().getId(), request.doctorId()));

        mockValidatorWithException(request, exception);

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation doctor");

        verify(reportValidator).validate(request);
    }


    @Test
    @DisplayName("delete - Should remove report when ID exists")
    void delete_ShouldRemoveReport_WhenSuccessful() {
        when(repository.existsById(EXISTING_ID)).thenReturn(true);
        doNothing().when(repository).deleteById(EXISTING_ID);

        service.delete(EXISTING_ID);

        verify(repository).existsById(EXISTING_ID);
        verify(repository).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("delete - Should throw not-found exception when report does not exist")
    void delete_shouldThrowNotFound_WhenIdDoesNotExists() {
        when(repository.existsById(NON_EXISTING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(REPORT_NOT_FOUND);
    }

    private static Stream<String> provideNonExistingTypes() {
        return Stream.of("Patient", "Doctor", "Operation");
    }

}