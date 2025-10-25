package com.kayky.domain.patient;

import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import com.kayky.domain.report.response.ReportBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {

    PatientBaseResponse toPatientBaseResponse(Patient patient);

    Patient toEntity(PatientBaseRequest postRequest);

    void updatePatientFromRequest(PatientBaseRequest putRequest, @MappingTarget Patient patient);

    @Named("toPatientInfo")
    default ReportBaseResponse.PatientInfo toPatientInfo(Patient patient) {
        if (patient == null) return null;
        return new ReportBaseResponse.PatientInfo(patient.getId(), patient.getFirstName());
    }

}
