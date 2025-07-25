package com.kayky.domain.patient;

import com.kayky.domain.patient.response.PatientGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "user", source = "patient")
    PatientGetResponse toPatientGetResponse(Patient patient);
}
