package com.kayky.domain.doctor;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.doctor.request.DoctorPostRequest;
import com.kayky.domain.doctor.request.DoctorPutRequest;
import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.doctor.response.DoctorPostResponse;
import com.kayky.domain.doctor.response.DoctorPutResponse;
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
    public DoctorGetResponse findById(@PathVariable Long id) {
        log.debug("Request to find a doctor by id {}", id);
        return service.findById(id);
    }

    @GetMapping
    public PageResponse<DoctorGetResponse> findAll(Pageable pageable) {
        log.debug("Request received to list all doctors");
        return service.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<DoctorPostResponse> save(@RequestBody DoctorPostRequest request) {
        log.debug("request to create new doctor");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorPutResponse> update(@RequestBody DoctorPutRequest request, @PathVariable Long id) {
        log.debug("Request to update doctor with id {}", id);

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }

}
