package com.kayky.domain.operation.response;

import com.kayky.domain.doctor.response.DoctorBaseResponse;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationBaseResponse {

    private Long id;
    private String description;
    private LocalDateTime scheduledAt;

    private DoctorBaseResponse doctor;
    private PatientBaseResponse patient;

    private OperationStatus status;
}
