package com.kayky.domain.doctor;

import com.kayky.domain.doctor.response.DoctorGetResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorGetResponse toDoctorGetResponse(Doctor doctor);
}
