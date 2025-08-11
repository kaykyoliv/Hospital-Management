package com.kayky.domain.operation;

import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.doctor.DoctorRepository;
import com.kayky.domain.operation.request.OperationBaseRequest;
import com.kayky.domain.operation.response.OperationBaseResponse;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.patient.PatientRepository;
import com.kayky.domain.user.UserValidator;
import com.kayky.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService {

    private final OperationRepository repository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserValidator userValidator;

    private final OperationMapper mapper;

    @Transactional(readOnly = true)
    public OperationBaseResponse findById(Long id){
        return repository.findById(id)
                .map(mapper::toOperationBaseResponse)
                .orElseThrow(() -> {
                    log.warn("Operation not found with id {}", id);

                    return new ResourceNotFoundException("Operation not found");
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<OperationDetailsResponse> findAll(Pageable pageable){
        var page = repository.findAllProjected(pageable);
        return PageUtils.mapPage(page, mapper::toOperationDetailsResponse);
    }

    @Transactional
    public OperationBaseResponse save(OperationBaseRequest request){
        userValidator.assertIfUserExist(request.getPatient().getId(), "Patient");
        userValidator.assertIfUserExist(request.getDoctor().getId(), "Doctor");

        var operationToSave = mapper.toEntity(request);
        var savedOperation = repository.save(operationToSave);

        log.info("New operation saved with ID {}", savedOperation.getId());

        return mapper.toOperationBaseResponse(savedOperation);
    }

    @Transactional
    public OperationBaseResponse update(OperationBaseRequest request, Long id){
        var operationToUpdate = repository.findById(id)
                        .orElseThrow(()-> {
                            log.warn("Cannot update: Operation not found with ID {}", id);

                            return new ResourceNotFoundException("Operation not found");
                        });

        userValidator.assertIfUserExist(request.getPatient().getId(), "Patient");
        userValidator.assertIfUserExist(request.getDoctor().getId(), "Doctor");

        var patient = patientRepository.findById(request.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        var doctor = doctorRepository.findById(request.getDoctor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        mapper.updateOperationFromRequest(request, operationToUpdate);

        operationToUpdate.setPatient(patient);
        operationToUpdate.setDoctor(doctor);

        var updatedOperation = repository.save(operationToUpdate);

        log.info("Operation updated with ID {}", updatedOperation.getId());
        return mapper.toOperationBaseResponse(updatedOperation);
    }

    @Transactional
    public void delete(Long id){
        assertIfOperationExist(id);
        repository.deleteById(id);
    }

    private void assertIfOperationExist(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Operation not found");
        }
    }
}
