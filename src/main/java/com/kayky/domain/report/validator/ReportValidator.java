package com.kayky.domain.report.validator;

import com.kayky.core.exception.OperationMismatchException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.doctor.Doctor;
import com.kayky.domain.doctor.DoctorRepository;
import com.kayky.domain.operation.Operation;
import com.kayky.domain.operation.OperationRepository;
import com.kayky.domain.patient.Patient;
import com.kayky.domain.patient.PatientRepository;
import com.kayky.domain.report.request.ReportBaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportValidator {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OperationRepository operationRepository;

    public ValidationResult validate(ReportBaseRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id=" + request.patientId()));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id=" + request.doctorId()));

        Operation operation = operationRepository.findById(request.operationId())
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found with id=" + request.operationId()));

        if (!operation.getPatient().getId().equals(patient.getId())) {
            throw new OperationMismatchException("Operation patient (id=" + operation.getPatient().getId() +
                    ") does not match request patient (id=" + patient.getId() + ")");
        }

        if (!operation.getDoctor().getId().equals(doctor.getId())) {
            throw new OperationMismatchException("Operation doctor (id=" + operation.getDoctor().getId() +
                    ") does not match request doctor (id=" + doctor.getId() + ")");
        }

        return new ValidationResult(patient, doctor, operation);
    }

    public record ValidationResult(Patient patient, Doctor doctor, Operation operation) {}
}
