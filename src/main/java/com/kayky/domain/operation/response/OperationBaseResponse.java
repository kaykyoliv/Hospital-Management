package com.kayky.domain.operation.response;

import com.kayky.domain.doctor.response.DoctorBaseResponse;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents an operation with doctor, patient and scheduling details")
public class OperationBaseResponse {

    @Schema(
            description = "Unique identifier of the operation",
            example = "42"
    )
    private Long id;

    @Schema(
            description = "Description of the operation",
            example = "Knee surgery for ligament reconstruction"
    )
    private String description;

    @Schema(
            description = "Scheduled date and time for the operation",
            example = "2025-10-15T14:30:00"
    )
    private LocalDateTime scheduledAt;

    @Schema(
            description = "Doctor assigned to the operation",
            implementation = DoctorBaseResponse.class
    )
    private DoctorBaseResponse doctor;

    @Schema(
            description = "Patient undergoing the operation",
            implementation = PatientBaseResponse.class
    )
    private PatientBaseResponse patient;

    @Schema(
            description = "Current status of the operation",
            example = "SCHEDULED"
    )
    private OperationStatus status;
}
