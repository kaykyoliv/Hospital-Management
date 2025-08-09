package com.kayky.domain.doctor;

import com.kayky.domain.doctor.request.DoctorPostRequest;
import com.kayky.domain.doctor.request.DoctorPutRequest;
import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.doctor.response.DoctorPageResponse;
import com.kayky.domain.doctor.response.DoctorPostResponse;
import com.kayky.domain.doctor.response.DoctorPutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorGetResponse toDoctorGetResponse(Doctor doctor);

    Doctor toEntity(DoctorPostRequest postRequest);

    DoctorPostResponse toDoctorPostResponse(Doctor doctor);

    DoctorPutResponse toDoctorPutResponse(Doctor doctor);

    void updateDoctorFromRequest(DoctorPutRequest putRequest, @MappingTarget Doctor doctor);

    default DoctorPageResponse toDoctorPageResponse(Page<Doctor> doctors){
        List<DoctorGetResponse> content = doctors.map(this::toDoctorGetResponse).getContent();

        return DoctorPageResponse.builder()
                .doctors(content)
                .totalPages(doctors.getTotalPages())
                .totalElements(doctors.getTotalElements())
                .currentPage(doctors.getNumber())
                .build();
    }
}
