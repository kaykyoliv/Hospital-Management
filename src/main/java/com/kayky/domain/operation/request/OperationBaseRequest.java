package com.kayky.domain.operation.request;

import com.kayky.domain.doctor.response.DoctorBaseResponse;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for creating or updating a surgical/medical operation")
public class OperationBaseRequest {

    @Schema(
            description = "Description of the operation",
            example = "Knee surgery for ligament reconstruction"
    )
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(
            description = "Scheduled date and time for the operation (must be in the future)",
            example = "2025-10-15T14:30:00"
    )
    @NotNull(message = "Scheduled date/time is required")
    @Future(message = "Scheduled date/time must be in the future")
    private LocalDateTime scheduledAt;

    @Schema(
            description = "Doctor assigned to perform the operation. Must contain at least the ID.",
            implementation = DoctorBaseResponse.class
    )
    @NotNull(message = "Doctor information is required")
    private DoctorBaseResponse doctor;

    @Schema(
            description = "Patient who will undergo the operation. Must contain at least the ID.",
            implementation = PatientBaseResponse.class
    )
    @NotNull(message = "Patient information is required")
    private PatientBaseResponse patient;

    @Schema(
            description = "Current status of the operation",
            example = "SCHEDULED"
    )
    @NotNull(message = "Status is required")
    private OperationStatus status;
}
