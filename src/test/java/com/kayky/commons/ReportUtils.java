package com.kayky.commons;

import com.kayky.domain.report.Report;
import com.kayky.domain.report.ReportStatus;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.response.ReportBaseResponse;
import com.kayky.domain.report.validator.ReportValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                .patient(PatientUtils.savedPatient(1L))
                .doctor(DoctorUtils.savedDoctor(2L))
                .operation(OperationUtils.savedOperation());

    }

    private static Report createReport(Long id, String title,  String description, String diagnosis, String treatmentPlan, LocalDate reportDate){
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

    public static List<Report> reportList() {
        return List.of(
                createReport(1L, "Annual Checkup", "Routine health examination", "Healthy", "No treatment needed",
                        LocalDate.of(2026, 12, 5)),
                createReport(2L, "Knee Injury", "Patient reported pain after running", "Ligament strain", "Physical therapy for 6 weeks",
                        LocalDate.of(2026, 11, 10)),
                createReport(3L, "Flu Symptoms", "Fever, cough, and sore throat", "Influenza", "Rest and hydration",
                        LocalDate.of(2026, 10, 20))
        );
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

    public static List<ReportBaseResponse> baseResponseList(){
         return reportList().stream().map(ReportUtils::asBaseResponse).toList();
    }

    public static ReportBaseRequest asBaseRequest(){
        return ReportBaseRequest.builder()
                .title("Pediatric Post-op Review")
                .description("Patient is stable and responding well to treatment")
                .diagnosis("Minor fracture treated successfully")
                .treatmentPlan("Continue physiotherapy sessions twice a week")
                .reportDate(LocalDate.of(2026, 7,4))
                .status(ReportStatus.FINALIZED)
                .patientId(1L)
                .doctorId(2L)
                .operationId(1L)
                .build();
    }

    public static ReportValidator.ValidationResult validationResult(){
        return new ReportValidator.ValidationResult(PatientUtils.savedPatient(1L), DoctorUtils.savedDoctor(2L), OperationUtils.savedOperation());
    }


}
