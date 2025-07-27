package com.kayky.domain.patient;

import com.kayky.domain.patient.request.PatientPostRequest;
import com.kayky.domain.patient.response.PatientGetResponse;
import com.kayky.domain.patient.response.PatientPostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "v1/patient")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<PatientGetResponse> findById(@PathVariable Long id) {
        log.debug("Request to find product by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PatientGetResponse>> findAllPaged(Pageable pageable) {
        log.debug("Request received to list all products");

        var response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PatientPostResponse> save(@RequestBody PatientPostRequest request) {
        log.debug("request to create new product");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

}
