package com.kayky.domain.report;


import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.doctor.DoctorMapper;
import com.kayky.domain.operation.Operation;
import com.kayky.domain.operation.OperationMapper;
import com.kayky.domain.patient.Patient;
import com.kayky.domain.patient.PatientMapper;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.response.ReportBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {PatientMapper.class, DoctorMapper.class, OperationMapper.class})
public interface ReportMapper {

    @Mapping(target = "patient", source = "patient", qualifiedByName = "toPatientInfo")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "toDoctorInfo")
    @Mapping(target = "operation", source = "operation", qualifiedByName = "toOperationInfo")
    ReportBaseResponse toReportBaseResponse(Report report);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "diagnosis", source = "request.diagnosis")
    @Mapping(target = "treatmentPlan", source = "request.treatmentPlan")
    @Mapping(target = "reportDate", source = "request.reportDate")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "operation", source = "operation")
    Report toEntity(ReportBaseRequest request, Patient patient, Doctor doctor, Operation operation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "diagnosis", source = "request.diagnosis")
    @Mapping(target = "treatmentPlan", source = "request.treatmentPlan")
    @Mapping(target = "reportDate", source = "request.reportDate")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "operation", source = "operation")
    void updateReportFromRequest(ReportBaseRequest request,Patient patient, Doctor doctor, Operation operation, @MappingTarget Report report);
}