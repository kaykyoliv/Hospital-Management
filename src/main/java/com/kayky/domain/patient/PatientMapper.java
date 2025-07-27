package com.kayky.domain.patient;

import com.kayky.domain.patient.request.PatientPostRequest;
import com.kayky.domain.patient.request.PatientPutRequest;
import com.kayky.domain.patient.response.PatientGetResponse;
import com.kayky.domain.patient.response.PatientPostResponse;
import com.kayky.domain.patient.response.PatientPutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientGetResponse toPatientGetResponse(Patient patient);

    Patient toEntity(PatientPostRequest postRequest);

    PatientPostResponse toPatientPostResponse(Patient patient);

    PatientPutResponse toPatientPutResponse(Patient patient);

    void updatePatientFromRequest(PatientPutRequest putRequest, @MappingTarget Patient patient);

    default Page<PatientGetResponse> toPageGetResponse(Page<Patient> patients) {
        return patients.map(this::toPatientGetResponse);
    }
}
