package com.kayky.domain.report.request;

import com.kayky.domain.report.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReportBaseRequest(

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotBlank
        String diagnosis,

        @NotNull
        String treatmentPlan,

        @NotNull
        LocalDate reportDate,

        @NotNull
        ReportStatus status,

        @NotNull
        Long patientId,

        @NotNull
        Long doctorId,

        @NotNull
        Long operationId

) {}
