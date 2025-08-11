package com.kayky.domain.operation.request;

import com.kayky.domain.doctor.response.DoctorBaseResponse;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class OperationBaseRequest {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Scheduled date/time is required")
    @Future(message = "Scheduled date/time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Doctor information is required")
    private DoctorBaseResponse doctor;

    @NotNull(message = "Patient information is required")
    private PatientBaseResponse patient;

    @NotNull(message = "Status is required")
    private OperationStatus status;
}
