package com.kayky.domain.payment;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.payment.request.PaymentBaseRequest;
import com.kayky.domain.payment.response.PaymentBaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentBaseResponse> findById(@PathVariable Long id) {
        log.debug("Request to find payment by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public PageResponse<PaymentBaseResponse> findAllPaged(Pageable pageable) {
        log.debug("Request received to list all payment");
        return service.findAll(pageable);
    }

    @GetMapping("/patients/{patientId}/payments")
    public ResponseEntity<List<PaymentBaseResponse>> findByPatient(@PathVariable Long patientId) {
        log.debug("Request to find all payments by patient id {}", patientId);

        var response = service.findByPatient(patientId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PaymentBaseResponse> save(@Valid @RequestBody PaymentBaseRequest request) {
        log.debug("Request to create new payment");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }
}
