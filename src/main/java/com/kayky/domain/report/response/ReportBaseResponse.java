package com.kayky.domain.report.response;

import com.kayky.domain.report.ReportStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ReportBaseResponse(
        Long id,
        String title,
        String description,
        String diagnosis,
        String treatmentPlan,
        LocalDate reportDate,
        ReportStatus status,
        PatientInfo patient,
        DoctorInfo doctor,
        OperationInfo operation,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

){
    public record PatientInfo(Long id, String name){}
    public record DoctorInfo(Long id, String name){}
    public record OperationInfo(
            Long id,
            String description,
            LocalDateTime scheduledAt,
            String status
    ) {}
}
