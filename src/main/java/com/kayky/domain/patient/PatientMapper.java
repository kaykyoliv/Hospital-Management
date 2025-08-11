package com.kayky.domain.patient;

import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientBaseResponse toPatientBaseResponse(Patient patient);

    Patient toEntity(PatientBaseRequest postRequest);

    void updatePatientFromRequest(PatientBaseRequest putRequest, @MappingTarget Patient patient);

}
