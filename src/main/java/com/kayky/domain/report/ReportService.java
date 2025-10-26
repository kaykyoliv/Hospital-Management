package com.kayky.domain.report;

import com.kayky.core.exception.OperationMismatchException;
import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.doctor.DoctorRepository;
import com.kayky.domain.operation.Operation;
import com.kayky.domain.operation.OperationRepository;
import com.kayky.domain.patient.Patient;
import com.kayky.domain.patient.PatientRepository;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.response.ReportBaseResponse;
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
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OperationRepository operationRepository;
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
        var request = validateReportRequest(postRequest);

        if (reportRepository.existsByOperationId(request.operation.getId())) {
            throw new ReportAlreadyExistsException(request.operation.getId());
        }

        var reportToSave = reportMapper.toEntity(postRequest, request.patient(), request.doctor(), request.operation());
        var savedReport = reportRepository.save(reportToSave);

        return reportMapper.toReportBaseResponse(savedReport);
    }

    @Transactional
    public ReportBaseResponse update(ReportBaseRequest putRequest, Long id) {

        var reportToUpdate = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        var request = validateReportRequest(putRequest);

        if (reportRepository.existsByOperationId(request.operation.getId())
                && !reportToUpdate.getOperation().getId().equals(request.operation.getId())) {
            throw new ReportAlreadyExistsException(request.operation.getId());
        }

        reportMapper.updateReportFromRequest(putRequest,
                request.patient(),
                request.doctor(),
                request.operation(),
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

    private ValidationResult validateReportRequest(ReportBaseRequest request) {
        var patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        var doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        var operation = operationRepository.findById(request.operationId())
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        if (!operation.getPatient().getId().equals(patient.getId())) {
            throw new OperationMismatchException("Operation patient does not match request patient");
        }

        if (doctor != null && !operation.getDoctor().getId().equals(doctor.getId())) {
            throw new OperationMismatchException("Operation doctor does not match request doctor");
        }

        return new ValidationResult(patient, doctor, operation);
    }

    private record ValidationResult(Patient patient, Doctor doctor, Operation operation) {
    }

}
