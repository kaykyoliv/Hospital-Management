package com.kayky.domain.report.request;

import com.kayky.domain.report.ReportStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReportBaseRequest(

        @NotBlank
        @Size(min = 3, max = 120)
        String title,

        @NotBlank
        @Size(min = 5, max = 1000)
        String description,

        @NotBlank
        @Size(min = 3, max = 500)
        String diagnosis,

        @NotBlank
        @Size(min = 3, max = 800)
        String treatmentPlan,

        @NotNull
        @PastOrPresent(message = "reportDate cannot be in the future")
        LocalDate reportDate,

        @NotNull
        ReportStatus status,

        @NotNull
        @Positive(message = "patientId must be greater than zero")
        Long patientId,

        @NotNull
        @Positive(message = "doctorId must be greater than zero")
        Long doctorId,

        @NotNull
        @Positive(message = "operationId must be greater than zero")
        Long operationId
) {}
