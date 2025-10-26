package com.kayky.domain.report;

import com.kayky.core.exception.ApiError;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.OperationMismatchException;
import com.kayky.core.exception.ValidationError;
import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.report.request.ReportBaseRequest;
import com.kayky.domain.report.response.ReportBaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "v1/report")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report", description = "Operations related to report management")
public class ReportController {

    private final ReportService service;

    @Operation(
            summary = "Find report by ID",
            description = "Retrieves report by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReportBaseResponse> findById(@PathVariable Long id) {
        log.debug("request to find report by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List all reports",
            description = "Returns a paginated list off all registered reports"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List off reports returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageResponse.class))
    )
    @GetMapping
    public PageResponse<ReportBaseResponse> findAllPaged(@ParameterObject Pageable pageable) {
        log.debug("request received to list all reports");
        return service.findAll(pageable);
    }

    @Operation(
            summary = "Register a new report",
            description = "Creates a new report and its ID and details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Report created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "a report already exists for the operation ID, or the operation does not match the provided patient/doctor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient, doctor or operation not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation error - invalid or missing fields in request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationError.class))
            )
    })
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

    @Operation(
            summary = "Update report information",
            description = "Updates a report’s details by its ID, including doctor, patient, or operation associations"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "a report already exists for the operation ID, or the operation does not match the provided patient/doctor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report, Patient, doctor or operation not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation error - invalid or missing fields in request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationError.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReportBaseResponse> update(@Valid @RequestBody ReportBaseRequest request, @PathVariable Long id) {
        log.debug("Request to update a report");

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete report by ID",
            description = "Delete report by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Report deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("Request to delete a report");

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
