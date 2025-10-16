package com.kayky.domain.patient;

import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import com.kayky.domain.user.UserValidator;
import com.kayky.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final UserValidator userValidator;

    @Transactional(readOnly = true)
    public PatientBaseResponse findById(Long id) {
        return patientRepository.findById(id)
                .map(patientMapper::toPatientBaseResponse)
                .orElseThrow(() -> {
                    log.warn("Patient not found with ID {}", id);

                   return new ResourceNotFoundException("Patient not found");
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientBaseResponse> findAll(Pageable pageable) {
        var paginatedPatients = patientRepository.findAll(pageable);
        return PageUtils.mapPage(paginatedPatients, patientMapper::toPatientBaseResponse);
    }

    @Transactional
    public PatientBaseResponse save(PatientBaseRequest postRequest) {
        userValidator.assertEmailDoesNotExist(postRequest.getEmail());

        var patientToSave = patientMapper.toEntity(postRequest);
        var patientSaved = patientRepository.save(patientToSave);

        log.info("New patient saved with ID {}", patientSaved.getId());

        return patientMapper.toPatientBaseResponse(patientSaved);
    }

    @Transactional
    public PatientBaseResponse update(PatientBaseRequest putRequest, Long id) {
        var patientToUpdate = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: Patient not found with ID {}", id);
                    return new ResourceNotFoundException("Patient not found");
                });


        userValidator.assertEmailDoesNotExist(putRequest.getEmail(), id);

        patientMapper.updatePatientFromRequest(putRequest, patientToUpdate);
        var updatedPatient = patientRepository.save(patientToUpdate);

        log.info("Patient updated with ID {}", updatedPatient.getId());
        return patientMapper.toPatientBaseResponse(updatedPatient);
    }
}
