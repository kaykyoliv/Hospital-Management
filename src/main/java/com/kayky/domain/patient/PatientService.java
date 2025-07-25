package com.kayky.domain.patient;

import com.kayky.domain.patient.response.PatientGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;

    @Transactional(readOnly = true)
    public PatientGetResponse findById(Long id){
        var patient = repository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toPatientGetResponse(patient);
    }

}
