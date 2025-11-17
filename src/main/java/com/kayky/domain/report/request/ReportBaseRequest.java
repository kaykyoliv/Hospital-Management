package com.kayky.domain.report.request;

import com.kayky.domain.report.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(
        name = "ReportBaseRequest",
        description = "Payload used to create or update a medical report associated with a patient, doctor and operation"
)
public record ReportBaseRequest(

        @Schema(
                description = "Short descriptive title for the report",
                example = "Post-operative recovery evaluation"
        )
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
        String title,

        @Schema(
                description = "Detailed description of the report and clinical notes",
                example = "Patient shows significant improvement in mobility and no signs of infection."
        )
        @NotBlank(message = "Description is required")
        @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
        String description,

        @Schema(
                description = "Medical diagnosis associated with the report",
                example = "Acute appendicitis with mild post-operative inflammation"
        )
        @NotBlank(message = "Diagnosis is required")
        @Size(min = 3, max = 500, message = "Diagnosis must be between 3 and 500 characters")
        String diagnosis,

        @Schema(
                description = "Treatment plan recommended by the doctor",
                example = "Continue antibiotics for 7 days and schedule follow-up appointment."
        )
        @NotBlank(message = "Treatment plan is required")
        @Size(min = 3, max = 800, message = "Treatment plan must be between 3 and 800 characters")
        String treatmentPlan,

        @Schema(
                description = "Date when the report was created or assigned",
                example = "2025-01-15"
        )
        @NotNull(message = "Report date is required")
        @PastOrPresent(message = "reportDate cannot be in the future")
        LocalDate reportDate,

        @Schema(
                description = "Status of the medical report",
                example = "COMPLETED"
        )
        @NotNull(message = "Status is required")
        ReportStatus status,

        @Schema(
                description = "Identifier of the patient linked to this report",
                example = "42"
        )
        @NotNull(message = "patientId is required")
        @Positive(message = "patientId must be greater than zero")
        Long patientId,

        @Schema(
                description = "Identifier of the doctor who authored the report",
                example = "7"
        )
        @NotNull(message = "doctorId is required")
        @Positive(message = "doctorId must be greater than zero")
        Long doctorId,

        @Schema(
                description = "Identifier of the medical operation related to the report",
                example = "15"
        )
        @NotNull(message = "operationId is required")
        @Positive(message = "operationId must be greater than zero")
        Long operationId
) {}
