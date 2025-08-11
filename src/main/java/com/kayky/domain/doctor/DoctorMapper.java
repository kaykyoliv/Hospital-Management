package com.kayky.domain.doctor;

import com.kayky.domain.doctor.request.DoctorBaseRequest;
import com.kayky.domain.doctor.response.DoctorBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorBaseResponse toDoctorBaseResponse(Doctor doctor);

    Doctor toEntity(DoctorBaseRequest postRequest);

    void updateDoctorFromRequest(DoctorBaseRequest putRequest, @MappingTarget Doctor doctor);
}
