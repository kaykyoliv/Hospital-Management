package com.kayky.domain.doctor;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.doctor.request.DoctorPostRequest;
import com.kayky.domain.doctor.request.DoctorPutRequest;
import com.kayky.domain.doctor.response.DoctorGetResponse;
import com.kayky.domain.doctor.response.DoctorPostResponse;
import com.kayky.domain.doctor.response.DoctorPutResponse;
import com.kayky.exception.ApiError;
import com.kayky.exception.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(value = "v1/doctor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Doctor", description = "Operations related to doctor management")
public class DoctorController {

    private final DoctorService service;

    @Operation(
            summary = "Find doctor by ID",
            description = "Retrieves doctor by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DoctorGetResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/{id}")
    public DoctorGetResponse findById(@PathVariable Long id) {
        log.debug("Request to find a doctor by id {}", id);
        return service.findById(id);
    }


    @Operation(
            summary = "List all doctor",
            description = "Returns a paginated list off all registered doctor."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List off doctors returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageResponse.class))
    )
    @GetMapping
    public PageResponse<DoctorGetResponse> findAll(Pageable pageable) {
        log.debug("Request received to list all doctors");
        return service.findAll(pageable);
    }


    @Operation(
            summary = "Register a new doctor",
            description = "Creates a new doctor and its ID and details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Doctor created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DoctorPostResponse.class))),

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
    public ResponseEntity<DoctorPostResponse> save(@Valid @RequestBody DoctorPostRequest request) {
        log.debug("request to create new doctor");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }


    @Operation(
            summary = "Update operation information",
            description = "Updates operation data based on its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Doctor updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DoctorPutResponse.class))),

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
    public ResponseEntity<DoctorPutResponse> update(@Valid @RequestBody DoctorPutRequest request, @PathVariable Long id) {
        log.debug("Request to update doctor with id {}", id);

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }

}
