package com.kayky.domain.operation;


import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.domain.operation.response.OperationGetResponse;
import com.kayky.domain.operation.response.OperationPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
