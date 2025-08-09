package com.kayky.domain.doctor.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DoctorPageResponse {

    private List<DoctorGetResponse> doctors;
    private int totalPages;
    private Long totalElements;
    private int currentPage;
}
