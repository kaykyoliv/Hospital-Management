package com.kayky.domain.patient;

import com.kayky.domain.patient.request.PatientPostRequest;
import com.kayky.domain.patient.request.PatientPutRequest;
import com.kayky.domain.patient.response.PatientGetResponse;
import com.kayky.domain.patient.response.PatientPostResponse;
import com.kayky.domain.patient.response.PatientPutResponse;
import com.kayky.exception.EmailAlreadyExistsException;
import com.kayky.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional(readOnly = true)
    public PatientGetResponse findById(Long id) {
        return patientRepository.findById(id)
                .map(patientMapper::toPatientGetResponse)
                .orElseThrow(() -> {
                    log.warn("Patient not found with ID {}", id);
                   return new ResourceNotFoundException("Patient not found");
                });
    }

    @Transactional(readOnly = true)
    public Page<PatientGetResponse> findAll(Pageable pageable) {
        var paginatedPatients = patientRepository.findAll(pageable);
        return patientMapper.toPageGetResponse(paginatedPatients);
    }

    @Transactional
    public PatientPostResponse save(PatientPostRequest postRequest) {
        assertEmailDoesNotExist(postRequest.getEmail());

        var patientToSave = patientMapper.toEntity(postRequest);
        var patientSaved = patientRepository.save(patientToSave);

        log.info("New patient saved with ID {}", patientSaved.getId());

        return patientMapper.toPatientPostResponse(patientSaved);
    }

    @Transactional
    public PatientPutResponse update(PatientPutRequest putRequest, Long id) {
        var patientToUpdate = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: Patient not found with ID {}", id);
                    return new ResourceNotFoundException("Patient not found");
                });


        assertEmailDoesNotExist(putRequest.getEmail(), id);

        patientMapper.updatePatientFromRequest(putRequest, patientToUpdate);
        var updatedPatient = patientRepository.save(patientToUpdate);

        log.info("Patient updated with ID {}", updatedPatient.getId());
        return patientMapper.toPatientPutResponse(updatedPatient);
    }

    private void assertEmailDoesNotExist(String email) {
        patientRepository.findByEmail(email).ifPresent(this::throwEmailExistsException);
    }

    private void assertEmailDoesNotExist(String email, Long id) {
        patientRepository.findByEmailAndIdNot(email, id).ifPresent(this::throwEmailExistsException);
    }

    private void throwEmailExistsException(Patient patient) {
        log.warn("Email conflict: {} already in use by patient ID {}", patient.getEmail(), patient.getId());

        throw new EmailAlreadyExistsException("Email %s already in use".formatted(patient.getEmail()));
    }
}
