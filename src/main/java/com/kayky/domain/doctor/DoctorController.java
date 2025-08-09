package com.kayky.domain.doctor;

import com.kayky.domain.doctor.request.DoctorPostRequest;
import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.doctor.response.DoctorPageResponse;
import com.kayky.domain.doctor.response.DoctorPostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "v1/doctor")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {

    private final DoctorService service;

    @GetMapping("/{id}")
    public ResponseEntity<DoctorGetResponse> findById(@PathVariable Long id) {
        log.debug("Request to find a doctor by id {}", id);
        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<DoctorPageResponse> findAll(Pageable pageable) {
        log.debug("Request received to list all doctors");

        var response = service.findAll(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DoctorPostResponse> save(@RequestBody DoctorPostRequest request) {
        log.debug("request to create new doctor");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

}
