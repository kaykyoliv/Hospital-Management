package com.kayky.domain.report;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.response.ReportBaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "v1/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService service;

    @GetMapping("/{id}")
    public ResponseEntity<ReportBaseResponse> findById(@PathVariable Long id){
        log.debug("request to find report by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public PageResponse<ReportBaseResponse> findAllPaged(Pageable pageable){
        log.debug("request received to list all reports");
        return  service.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<ReportBaseResponse> save(@Valid @RequestBody ReportBaseRequest request) {
        log.debug("Request to create new report");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportBaseResponse> update(@Valid @RequestBody ReportBaseRequest request, @PathVariable Long id) {
        log.debug("Request to update a report");

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }
}
