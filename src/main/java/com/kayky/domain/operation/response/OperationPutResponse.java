package com.kayky.domain.operation.response;

import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.patient.response.PatientGetResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationPutResponse {

    private Long id;
    private String description;
    private LocalDateTime scheduledAt;

    private DoctorGetResponse doctor;
    private PatientGetResponse patient;

    private OperationStatus status;
}
