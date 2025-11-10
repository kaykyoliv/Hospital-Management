package com.kayky.domain.cashier;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.cashier.response.CashierBaseResponse;
import com.kayky.domain.patient.response.PatientBaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/cashier")
@RequiredArgsConstructor
@Slf4j
public class CashierController {

    private final CashierService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<CashierBaseResponse> findById(@PathVariable Long id) {
        log.debug("Request to find cashier by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public PageResponse<CashierBaseResponse> findAllPaged(Pageable pageable) {
        log.debug("Request received to list all cashiers");
        return service.findAll(pageable);
    }
}
