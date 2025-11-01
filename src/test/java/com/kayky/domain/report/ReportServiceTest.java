package com.kayky.domain.report;

import com.kayky.commons.PageUtils;
import com.kayky.commons.ReportUtils;
import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.validator.ReportValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void findALl_ShouldReturnPageResponse_WhenReportExist() {
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

        var request = ReportUtils.asBaseRequest();
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
        var request = ReportUtils.asBaseRequest();
        var validatorResult = ReportUtils.validationResult();

        when(reportValidator.validate(request)).thenReturn(validatorResult);

        when(repository.existsByOperationId(validatorResult.operation().getId())).thenReturn(true);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ReportAlreadyExistsException.class)
                .hasMessage(REPORT_ALREADY_EXISTS.formatted(request.operationId()));
    }


}