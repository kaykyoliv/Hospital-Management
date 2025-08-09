package com.kayky.domain.doctor;

import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.doctor.response.DoctorPageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorGetResponse toDoctorGetResponse(Doctor doctor);

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
