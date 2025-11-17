package com.kayky.domain.operation.response;

import com.kayky.domain.operation.OperationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detailed view of an operation with simplified doctor and patient information")
public class OperationDetailsResponse {

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
            description = "Full name of the doctor performing the operation",
            example = "Dr. Emily Johnson"
    )
    private String doctorName;

    @Schema(
            description = "Full name of the patient undergoing the operation",
            example = "Michael Carter"
    )
    private String patientName;

    @Schema(
            description = "Current status of the operation",
            example = "SCHEDULED"
    )
    private OperationStatus status;
}
