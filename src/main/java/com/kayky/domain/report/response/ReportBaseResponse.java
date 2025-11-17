package com.kayky.domain.report.response;

import com.kayky.domain.report.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(
        name = "ReportBaseResponse",
        description = "Represents the core information of a medical report, including patient, doctor and operation details."
)
@Builder
public record ReportBaseResponse(

        @Schema(description = "Unique identifier of the report", example = "15")
        Long id,

        @Schema(description = "Short descriptive title of the report",
                example = "Post-operative evaluation")
        String title,

        @Schema(description = "Detailed clinical description of the report findings",
                example = "Patient is recovering well with stable vital signs and reduced pain levels.")
        String description,

        @Schema(description = "Medical diagnosis established for this report",
                example = "Post-operative inflammation with expected recovery progress")
        String diagnosis,

        @Schema(description = "Treatment plan assigned by the doctor",
                example = "Maintain prescribed antibiotics and schedule follow-up in 7 days")
        String treatmentPlan,

        @Schema(description = "Date when the report was registered",
                example = "2025-01-20")
        LocalDate reportDate,

        @Schema(description = "Current status of the medical report",
                example = "COMPLETED")
        ReportStatus status,

        @Schema(description = "Information about the patient associated with the report")
        PatientInfo patient,

        @Schema(description = "Information about the doctor responsible for the report")
        DoctorInfo doctor,

        @Schema(description = "Details about the related medical operation")
        OperationInfo operation,

        @Schema(description = "Date and time when the report was created",
                example = "2025-01-20T14:32:11")
        LocalDateTime createdAt,

        @Schema(description = "Date and time when the report was last updated",
                example = "2025-01-22T09:18:44")
        LocalDateTime updatedAt
) {

    @Schema(name = "ReportPatientInfo", description = "Basic information about the patient")
    public record PatientInfo(
            @Schema(description = "Patient identifier", example = "42")
            Long id,

            @Schema(description = "Patient name", example = "Lucas Andrade")
            String name
    ) {}

    @Schema(name = "ReportDoctorInfo", description = "Basic information about the doctor who authored the report")
    public record DoctorInfo(
            @Schema(description = "Doctor identifier", example = "7")
            Long id,

            @Schema(description = "Doctor name", example = "Dra. Mariana Castro")
            String name
    ) {}

    @Schema(name = "ReportOperationInfo", description = "Summary of the medical operation related to the report")
    public record OperationInfo(
            @Schema(description = "Operation identifier", example = "11")
            Long id,

            @Schema(description = "Short description of the operation",
                    example = "Appendectomy procedure")
            String description,

            @Schema(description = "Date and time of the scheduled operation",
                    example = "2025-01-18T09:00:00")
            LocalDateTime scheduledAt,

            @Schema(description = "Current operation status",
                    example = "FINISHED")
            String status
    ) {}
}
