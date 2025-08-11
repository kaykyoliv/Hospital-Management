package com.kayky.domain.patient;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.domain.patient.response.PatientBaseResponse;
import com.kayky.exception.ApiError;
import com.kayky.exception.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "v1/patient")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patients", description = "Operations related to patient management")
public class PatientController {

    private final PatientService service;

    @Operation(
            summary = "Find patient by ID",
            description = "Retrieves patient details by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PatientBaseResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<PatientBaseResponse> findById(@PathVariable Long id) {
        log.debug("Request to find patient by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "List all patients",
            description = "Returns a paginated list off all registered patients.")
    @ApiResponse(
            responseCode = "200",
            description = "List of all patients returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = PageResponse.class)))
    )
    @GetMapping
    public PageResponse<PatientBaseResponse> findAllPaged(Pageable pageable) {
        log.debug("Request received to list all patients");
        return service.findAll(pageable);
    }


    @Operation(
            summary = "Register a new patient",
            description = "Creates a new patient and returns its ID and details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Patient created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PatientBaseResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed JSON or invalid enum",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationError.class)))
    })
    @PostMapping
    public ResponseEntity<PatientBaseResponse> save(@Valid @RequestBody PatientBaseRequest request) {
        log.debug("Request to create new patient");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }


    @Operation(
            summary = "Update patient information",
            description = "Updates patient data based on its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PatientBaseResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed JSON or invalid enum",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientBaseResponse> update(@Valid @RequestBody PatientBaseRequest request, @PathVariable Long id) {
        log.debug("Request to update patient with id {}", id);

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }
}
