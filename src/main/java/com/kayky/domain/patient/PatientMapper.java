package com.kayky.domain.patient;

import com.kayky.domain.patient.request.PatientPostRequest;
import com.kayky.domain.patient.response.PatientGetResponse;
import com.kayky.domain.patient.response.PatientPostResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientGetResponse toPatientGetResponse(Patient patient);

    default Page<PatientGetResponse> toPageGetResponse(Page<Patient> patients) {
        return patients.map(this::toPatientGetResponse);
    }

    Patient toEntity(PatientPostRequest postRequest);

    PatientPostResponse toPatientPostResponse(Patient patient);
}
