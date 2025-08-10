package com.kayky.domain.operation;

import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.doctor.DoctorRepository;
import com.kayky.domain.operation.request.OperationPostRequest;
import com.kayky.domain.operation.request.OperationPutRequest;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import com.kayky.domain.operation.response.OperationPostResponse;
import com.kayky.domain.operation.response.OperationPutResponse;
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
    public OperationGetResponse findById(Long id){
        return repository.findById(id)
                .map(mapper::toOperationGetResponse)
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


    public OperationPostResponse save(OperationPostRequest request){
        userValidator.assertIfUserExist(request.getPatient().getId(), "Patient");
        userValidator.assertIfUserExist(request.getDoctor().getId(), "Doctor");

        var operationToSave = mapper.toEntity(request);
        var savedOperation = repository.save(operationToSave);

        return mapper.toOperationPostResponse(savedOperation);
    }

    public OperationPutResponse update(OperationPutRequest request, Long id){
        var operationToUpdate = repository.findById(id)
                        .orElseThrow(()-> new ResourceNotFoundException("Operation not found"));

        userValidator.assertIfUserExist(request.getPatient().getId(), "Patient");
        userValidator.assertIfUserExist(request.getDoctor().getId(), "Doctor");

        var patient = patientRepository.findById(request.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        var doctor = doctorRepository.findById(request.getDoctor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        mapper.updateOperationFromRequest(request, operationToUpdate);

        operationToUpdate.setPatient(patient);
        operationToUpdate.setDoctor(doctor);

        var savedOperation = repository.save(operationToUpdate);

        return mapper.toOperationPutResponse(savedOperation);
    }
}
