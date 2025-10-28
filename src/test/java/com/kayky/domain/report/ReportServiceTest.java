package com.kayky.domain.report;

import com.kayky.commons.ReportUtils;
import com.kayky.domain.doctor.DoctorMapper;
import com.kayky.domain.operation.OperationMapper;
import com.kayky.domain.patient.PatientMapper;
import com.kayky.domain.report.validator.ReportValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.kayky.commons.TestConstants.EXISTING_ID;
import static org.mockito.Mockito.verify;

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
    void setUp(){
        service = new ReportService(repository, reportValidator, mapper);
    }

    @Test
    @DisplayName("findById: Should return ReportBaseResponse when the report exists")
    void findById_ShouldReturnReportBaseRequest_WhenReportExists(){
        var savedReport = ReportUtils.savedReport();

        BDDMockito.when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedReport));

        BDDMockito.when(mapper.toReportBaseResponse(savedReport))
                .thenReturn(ReportUtils.asBaseResponse(savedReport));


        var response = service.findById(EXISTING_ID);

        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        Assertions.assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }

}