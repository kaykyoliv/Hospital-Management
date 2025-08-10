package com.kayky.domain.operation;


import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.operation.request.OperationPostRequest;
import com.kayky.domain.operation.request.OperationPutRequest;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import com.kayky.domain.operation.response.OperationPostResponse;
import com.kayky.domain.operation.response.OperationPutResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "v1/operation")
@RequiredArgsConstructor
@Slf4j
public class OperationController {

    private final OperationService service;

    @GetMapping("/{id}")
    public OperationGetResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public PageResponse<OperationDetailsResponse> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<OperationPostResponse> save(@Valid @RequestBody OperationPostRequest request){
        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationPutResponse> update(@Valid @RequestBody OperationPutRequest request, @PathVariable Long id){
        var response = service.update(request, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
