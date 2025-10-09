package com.kayky.commons;

import com.kayky.domain.operation.Operation;
import com.kayky.domain.operation.Operation.OperationBuilder;
import com.kayky.domain.operation.OperationProjection;
import com.kayky.domain.operation.OperationStatus;
import com.kayky.domain.operation.request.OperationBaseRequest;
import com.kayky.domain.operation.response.OperationBaseResponse;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static com.kayky.commons.TestConstants.EXISTING_ID;

public class OperationUtils {

    // ========== OPERATION ENTITIES ==========

    public static OperationBuilder createOperationBuilder(Long id) {
        return Operation.builder()
                .id(id)
                .description("Cirurgia cardíaca de alta complexidade")
                .scheduledAt(LocalDateTime.of(2025, 9, 10, 14, 30))
                .doctor(DoctorUtils.savedDoctor(id))
                .patient(PatientUtils.savedPatient(id))
                .status(OperationStatus.SCHEDULED);
    }

    private static Operation createOperation(Long id, String description, LocalDateTime scheduledAt, OperationStatus status) {
        return createOperationBuilder(id)
                .description(description)
                .scheduledAt(scheduledAt)
                .status(status)
                .build();
    }

    public static Operation savedOperation() {
        return createOperationBuilder(EXISTING_ID).build();
    }

    public static Operation updatedOperation() {
        return createOperationBuilder(EXISTING_ID)
                .description("updated")
                .doctor(DoctorUtils.updatedDoctor())
                .build();
    }



    public static List<Operation> operationList() {
        return List.of(
                createOperation(1L, "Cirurgia cardíaca de alta complexidade",
                        LocalDateTime.of(2025, 9, 10, 14, 30), OperationStatus.SCHEDULED),
                createOperation(2L, "Cirurgia ortopédica no joelho esquerdo",
                        LocalDateTime.of(2025, 10, 5, 9, 0), OperationStatus.COMPLETED),
                createOperation(3L, "Procedimento dermatológico para remoção de lesão",
                        LocalDateTime.of(2025, 11, 12, 16, 15), OperationStatus.CANCELED)
        );
    }

    // ========== RESPONSE DTOs ==========

    public static List<OperationBaseResponse> operationBaseResponseList() {
        return mapOperations(OperationUtils::asBaseResponse);
    }

    public static OperationBaseResponse asBaseResponse(Operation operation) {
        return OperationBaseResponse.builder()
                .id(operation.getId())
                .description(operation.getDescription())
                .scheduledAt(operation.getScheduledAt())
                .doctor(DoctorUtils.asBaseResponse(operation.getDoctor()))
                .patient(PatientUtils.asBaseResponse(operation.getPatient()))
                .status(operation.getStatus())
                .build();
    }

    public static List<OperationDetailsResponse> operationDetailsResponseList() {
        return List.of(
                createDetailsResponse(
                        1L,
                        "Cirurgia cardíaca de alta complexidade",
                        LocalDateTime.of(2025, 9, 10, 14, 30),
                        "Robert",
                        "John",
                        OperationStatus.SCHEDULED
                ),
                createDetailsResponse(
                        2L,
                        "Cirurgia ortopédica de joelho",
                        LocalDateTime.of(2025, 10, 15, 9, 0),
                        "Emily",
                        "Jane",
                        OperationStatus.IN_PROGRESS
                ),
                createDetailsResponse(
                        3L,
                        "Cirurgia neurológica de alta precisão",
                        LocalDateTime.of(2025, 11, 20, 16, 45),
                        "Michael",
                        "Alice",
                        OperationStatus.CANCELED)
        );
    }

    private static OperationDetailsResponse createDetailsResponse(Long id, String description, LocalDateTime scheduledAt, String doctorName, String patientName, OperationStatus status) {
        return OperationDetailsResponse.builder()
                .id(id)
                .description(description)
                .scheduledAt(scheduledAt)
                .doctorName(doctorName)
                .patientName(patientName)
                .status(status)
                .build();
    }

    public static OperationBaseRequest asBaseRequest() {
        return OperationBaseRequest.builder()
                .description("Cirurgia cardíaca de alta complexidade")
                .scheduledAt(LocalDateTime.of(2025, 9, 10, 14, 30))
                .doctor(DoctorUtils.asBaseResponse(DoctorUtils.savedDoctor(1L)))
                .patient(PatientUtils.asBaseResponse(PatientUtils.savedPatient(1L)))
                .status(OperationStatus.SCHEDULED)
                .build();
    }

    // ========== PROJECTIONS ==========

    private static OperationProjection createOperationProjection(Long id, String description, LocalDateTime scheduledAt, String doctorFirstName, String patientFirstName, OperationStatus status) {
        return new OperationProjection() {
            @Override public Long getId() { return id; }
            @Override public String getDescription() { return description; }
            @Override public LocalDateTime getScheduledAt() { return scheduledAt; }
            @Override public String getDoctorFirstName() { return doctorFirstName; }
            @Override public String getPatientFirstName() { return patientFirstName; }
            @Override public OperationStatus getStatus() { return status; }
        };
    }

    public static List<OperationProjection> operationProjectionList() {
        return List.of(
                createOperationProjection(
                        1L,
                        "Cirurgia cardíaca de alta complexidade",
                        LocalDateTime.of(2025, 9, 10, 14, 30),
                        "Robert",
                        "John",
                        OperationStatus.SCHEDULED
                ),
                createOperationProjection(
                        2L,
                        "Cirurgia ortopédica de joelho",
                        LocalDateTime.of(2025, 10, 15, 9, 0),
                        "Emily",
                        "Jane",
                        OperationStatus.IN_PROGRESS
                ),
                createOperationProjection(
                        3L,
                        "Cirurgia neurológica de alta precisão",
                        LocalDateTime.of(2025, 11, 20, 16, 45),
                        "Michael",
                        "Alice",
                        OperationStatus.CANCELED
                )
        );
    }

    public static Page<OperationProjection> operationProjectionPage() {
        List<OperationProjection> projections = operationProjectionList();
        return new PageImpl<>(projections, PageRequest.of(0, 10), projections.size());
    }


    // ========== UTILITY METHODS ==========

    private static <R> List<R> mapOperations(Function<Operation, R> mapper) {
        return operationList().stream()
                .map(mapper)
                .toList();
    }

}
