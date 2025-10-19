package com.kayky.domain.doctor;

import com.kayky.domain.doctor.request.DoctorBaseRequest;
import com.kayky.domain.doctor.response.DoctorBaseResponse;
import com.kayky.domain.report.response.ReportBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorBaseResponse toDoctorBaseResponse(Doctor doctor);

    Doctor toEntity(DoctorBaseRequest postRequest);

    void updateDoctorFromRequest(DoctorBaseRequest putRequest, @MappingTarget Doctor doctor);

    @Named("toDoctorInfo")
    default ReportBaseResponse.DoctorInfo toDoctorInfo(Doctor doctor){
        if(doctor == null) return null;
        return new ReportBaseResponse.DoctorInfo(
                doctor.getId(),
                doctor.getFirstName()
        );
    }
}
