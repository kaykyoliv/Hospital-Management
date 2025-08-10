package com.kayky.domain.operation.request;

import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.patient.response.PatientGetResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class OperationPutRequest {

    private String description;
    private LocalDateTime scheduledAt;

    private DoctorGetResponse doctor;
    private PatientGetResponse patient;

    private OperationStatus status;
}
