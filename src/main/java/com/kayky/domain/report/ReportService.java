package com.kayky.domain.report;

import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.response.ReportBaseResponse;
import com.kayky.domain.report.validator.ReportValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportValidator reportValidator;
    private final ReportMapper reportMapper;

    @Transactional(readOnly = true)
    public ReportBaseResponse findById(Long id) {
        return reportRepository.findById(id)
                .map(reportMapper::toReportBaseResponse)
                .orElseThrow(() -> {
                    log.warn("Report not found with ID {}", id);

                    return new ResourceNotFoundException("Report not found");
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportBaseResponse> findAll(Pageable pageable) {
        var paginatedReport = reportRepository.findAll(pageable);
        return PageUtils.mapPage(paginatedReport, reportMapper::toReportBaseResponse);
    }


    @Transactional
    public ReportBaseResponse save(ReportBaseRequest postRequest) {
        var validation = reportValidator.validate(postRequest);

        if (reportRepository.existsByOperationId(validation.operation().getId())) {
            throw new ReportAlreadyExistsException(validation.operation().getId());
        }

        var reportToSave = reportMapper.toEntity(postRequest, validation.patient(), validation.doctor(), validation.operation());
        var savedReport = reportRepository.save(reportToSave);

        return reportMapper.toReportBaseResponse(savedReport);
    }

    @Transactional
    public ReportBaseResponse update(ReportBaseRequest putRequest, Long id) {

        var reportToUpdate = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var validation = reportValidator.validate(putRequest);

        if (reportRepository.existsByOperationId(validation.operation().getId())
                && !reportToUpdate.getOperation().getId().equals(validation.operation().getId())) {
            throw new ReportAlreadyExistsException(validation.operation().getId());
        }

        reportMapper.updateReportFromRequest(putRequest,
                validation.patient(),
                validation.doctor(),
                validation.operation(),
                reportToUpdate
        );

        var updatedReport = reportRepository.save(reportToUpdate);

        return reportMapper.toReportBaseResponse(updatedReport);
    }

    @Transactional
    public void delete(Long id){
        assertIfOperationExist(id);
        reportRepository.deleteById(id);
    }

    private void assertIfOperationExist(Long id){
        if(!reportRepository.existsById(id)){
            throw new ResourceNotFoundException("Report not found");
        }
    }
}
