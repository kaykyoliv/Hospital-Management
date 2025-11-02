package com.kayky.domain.report;

import com.kayky.commons.OperationUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.ReportUtils;
import com.kayky.core.exception.OperationMismatchException;
import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
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

    @Test
    @DisplayName("findById: Should return ReportBaseResponse when the report exists")
    void findById_ShouldReturnReportBaseResponse_WhenReportExists() {
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
    @DisplayName("findById: Should throw ResourceNotFoundException when the Report does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenReportDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(REPORT_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll: Should return PageResponse when reports exist")
    void findAll_ShouldReturnPageResponse_WhenReportExist() {
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
    @DisplayName("save: should return ReportBaseResponse when data is valid")
    void save_ShouldReturnReportBaseResponse_WhenDataIsValid() {
        var validatorResult = ReportUtils.validationResult();
        var savedReport = ReportUtils.savedReport();

        var request = asBaseRequest();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        when(reportValidator.validate(request)).thenReturn(validatorResult);
        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(false);
        when(mapper.toEntity(request, validatorResult.patient(), validatorResult.doctor(), validatorResult.operation()))
                .thenReturn(savedReport);
        when(repository.save(savedReport)).thenReturn(savedReport);
        when(mapper.toReportBaseResponse(savedReport)).thenReturn(expectedResponse);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save: should throw ReportAlreadyExistsException when report already exists")
    void save_ShouldThrow_WhenReportAlreadyExists() {
        var request = asBaseRequest();
        var validatorResult = ReportUtils.validationResult();

        when(reportValidator.validate(request)).thenReturn(validatorResult);

        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(true);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ReportAlreadyExistsException.class)
                .hasMessage(REPORT_ALREADY_EXISTS.formatted(request.operationId()));
    }

    @ParameterizedTest(name = "save: should throw ResourceNotFoundException when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void save_ShouldThrowResourceNotFoundException_WhenNonExistingType(String nonExistingType) {
        var request = asBaseRequest();

        when(reportValidator.validate(request))
                .thenThrow(new ResourceNotFoundException(nonExistingType + " not found"));

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingType);
    }

    @Test
    @DisplayName("save: propagates OperationMismatchException from validator")
    void save_ShouldPropagateOperationMismatchException_WhenOperationPatientDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        when(reportValidator.validate(request))
                .thenThrow(new OperationMismatchException(String.format(OPERATION_PATIENT_MISMATCH, savedOperation.getPatient().getId(), request.patientId())));

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation patient");

        verify(reportValidator).validate(request);
    }

    @Test
    @DisplayName("save: propagates OperationMismatchException from validator")
    void save_ShouldPropagateOperationMismatchException_WhenOperationDoctorDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        when(reportValidator.validate(request))
                .thenThrow(new OperationMismatchException(String.format(OPERATION_DOCTOR_MISMATCH, savedOperation.getPatient().getId(), request.patientId())));

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation doctor");

        verify(reportValidator).validate(request);
    }


    @Test
    @DisplayName("update: Should return ReportBaseResponse when update is valid")
    void update_ShouldReturnReportBaseResponse_WhenUpdateIsValid() {
        var validatorResult = ReportUtils.validationResult();
        var savedReport = ReportUtils.savedReport();

        var request = ReportUtils.asBaseRequest();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        when(reportValidator.validate(request)).thenReturn(validatorResult);
        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(false);

        when(repository.save(any(Report.class))).thenReturn(savedReport);

        when(mapper.toReportBaseResponse(savedReport)).thenReturn(expectedResponse);

        var result = service.update(request, EXISTING_ID);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(reportValidator).validate(request);
        verify(repository).save(any(Report.class));
        verify(mapper).toReportBaseResponse(savedReport);
    }

    @ParameterizedTest(name = "update: should throw ResourceNotFoundException when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void update_ShouldThrowResourceNotFoundException_WhenNonExistingType(String nonExistingType) {
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
    @DisplayName("update: should throw ReportAlreadyExistsException when report already exists")
    void update_ShouldThrow_WhenReportAlreadyExists() {
        var request = asBaseRequest();
        var validatorResult = ReportUtils.validationResult();
        var savedReport = ReportUtils.savedReport();
        savedReport.getOperation().setId(NON_EXISTING_ID);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));
        when(reportValidator.validate(request)).thenReturn(validatorResult);

        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(true);

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(ReportAlreadyExistsException.class)
                .hasMessage(REPORT_ALREADY_EXISTS.formatted(request.operationId()));
    }

    private static Stream<String> provideNonExistingTypes() {
        return Stream.of("Patient", "Doctor", "Operation");
    }


    @Test
    @DisplayName("update: propagates OperationMismatchException from validator")
    void update_ShouldPropagateOperationMismatchException_WhenOperationPatientDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        var savedReport = ReportUtils.savedReport();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        when(reportValidator.validate(request))
                .thenThrow(new OperationMismatchException(String.format(OPERATION_PATIENT_MISMATCH, savedOperation.getPatient().getId(), request.patientId())));

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation patient");

        verify(reportValidator).validate(request);
    }

    @Test
    @DisplayName("update: propagates OperationMismatchException from validator")
    void update_ShouldPropagateOperationMismatchException_WhenOperationDoctorDoesNotMatch() {
        var request = ReportUtils.asBaseRequest();
        var savedOperation = OperationUtils.savedOperation();

        var savedReport = ReportUtils.savedReport();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        when(reportValidator.validate(request))
                .thenThrow(new OperationMismatchException(String.format(OPERATION_DOCTOR_MISMATCH, savedOperation.getPatient().getId(), request.patientId())));

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(OperationMismatchException.class)
                .hasMessageContaining("Operation doctor");

        verify(reportValidator).validate(request);
    }


    @Test
    @DisplayName("delete: should remove report when ID exists")
    void delete_ShouldRemoveReport_WhenSuccessful() {
        when(repository.existsById(EXISTING_ID)).thenReturn(true);
        doNothing().when(repository).deleteById(EXISTING_ID);

        service.delete(EXISTING_ID);

        verify(repository).existsById(EXISTING_ID);
        verify(repository).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("delete: should throw ResourceNotFoundException when ID does not exist")
    void delete_ShouldThrowResourceNotFoundException_WhenIdDoesNotExists() {
        when(repository.existsById(NON_EXISTING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(REPORT_NOT_FOUND);
    }
}