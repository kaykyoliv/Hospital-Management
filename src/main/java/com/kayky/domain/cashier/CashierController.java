package com.kayky.domain.cashier;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.cashier.request.CashierBaseRequest;
import com.kayky.domain.cashier.response.CashierBaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

    @PostMapping
    public ResponseEntity<CashierBaseResponse> save(@Valid @RequestBody CashierBaseRequest request) {
        log.debug("Request to create new cashier");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CashierBaseResponse> update(@Valid @RequestBody CashierBaseRequest request, @PathVariable Long id) {
        log.debug("Request to update cashier with id {}", id);

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }
}
