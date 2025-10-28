package com.kayky.commons;

import com.kayky.domain.report.Report;
import com.kayky.domain.report.ReportStatus;
import com.kayky.domain.report.response.ReportBaseResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.kayky.commons.TestConstants.EXISTING_ID;

public class ReportUtils {

    private static Report.ReportBuilder createReportBuilder(Long id){
        return Report.builder()
                .id(id)
                .title("Pediatric Post-op Review")
                .description("Patient is stable and responding well to treatment")
                .diagnosis("Minor fracture treated successfully")
                .treatmentPlan("Continue physiotherapy sessions twice a week")
                .reportDate(LocalDate.of(2026, 7,4))
                .status(ReportStatus.FINALIZED)
                .patient(PatientUtils.savedPatient(id))
                .doctor(DoctorUtils.savedDoctor(id))
                .operation(OperationUtils.savedOperation());

    }

    private Report createReport(Long id, String title,  String description, String diagnosis, String treatmentPlan, LocalDate reportDate){
        return createReportBuilder(id)
                .title(title)
                .description(description)
                .diagnosis(diagnosis)
                .treatmentPlan(treatmentPlan)
                .reportDate(reportDate)
                .build();
    }

    public static Report savedReport(){
        return createReportBuilder(EXISTING_ID).build();
    }

    public static ReportBaseResponse asBaseResponse(Report report){
        return ReportBaseResponse.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .diagnosis(report.getDiagnosis())
                .treatmentPlan(report.getTreatmentPlan())
                .reportDate(report.getReportDate())
                .status(report.getStatus())
                .patient(new ReportBaseResponse.PatientInfo(
                        report.getPatient().getId(),
                        report.getPatient().getFirstName()
                ))
                .doctor(new ReportBaseResponse.DoctorInfo(
                        report.getDoctor().getId(),
                        report.getDoctor().getFirstName()
                ))
                .operation(new ReportBaseResponse.OperationInfo(
                        report.getOperation().getId(),
                        report.getOperation().getDescription(),
                        report.getOperation().getScheduledAt(),
                        report.getOperation().getStatus().name()
                ))
                .build();
    }


}
