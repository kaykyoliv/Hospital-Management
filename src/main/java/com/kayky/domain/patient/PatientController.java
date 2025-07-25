package com.kayky.domain.patient;

import com.kayky.domain.patient.response.PatientGetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/patient")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<PatientGetResponse> findById(@PathVariable Long id){
        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

}
