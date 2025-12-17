package com.kayky.domain.operation;


import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.operation.request.OperationBaseRequest;
import com.kayky.domain.operation.response.OperationBaseResponse;
import com.kayky.domain.operation.response.OperationDetailsResponse;
import com.kayky.core.exception.ApiError;
import com.kayky.core.exception.ValidationError;
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
@RequestMapping(value = "v1/operation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Operation", description = "Operations related to operation management")
public class OperationController {

    private final OperationService service;

    @Operation(
            summary = "Find operation by ID",
            description = "Retrieves operation by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operation found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationBaseResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Operation not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/{id}")
    public OperationBaseResponse findById(@PathVariable Long id) {
        log.debug("Request to find a operation by id {}", id);

        return service.findById(id);
    }


    @Operation(
            summary = "List all operations",
            description = "Returns a paginated list off all registered operations."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List off operations returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageResponse.class))
    )
    @GetMapping
    public PageResponse<OperationDetailsResponse> findAll(Pageable pageable) {
        log.debug("Request received to list all operations details");

        return service.findAll(pageable);
    }


    @Operation(
            summary = "Register a new operation",
            description = "Creates a new operation and its ID and details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Operation created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationBaseResponse.class))),

            @ApiResponse(
                    responseCode = "400",
                    description = "Malformed JSON or invalid enum",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or Patient not found (ID does not exist)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business validation error: ID exists but does not belong to the correct user type (e.g., doctorId refers to a Patient)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationError.class))),
    })
    @PostMapping
    public ResponseEntity<OperationBaseResponse> save(@Valid @RequestBody OperationBaseRequest request) {
        log.debug("request to create new operation");

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
                    description = "Operation updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationBaseResponse.class))),

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
    public ResponseEntity<OperationBaseResponse> update(@Valid @RequestBody OperationBaseRequest request, @PathVariable Long id) {
        log.debug("Request to update operation with id {}", id);

        var response = service.update(request, id);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Delete operation by ID",
            description = "Delete operation by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Operation deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Operation not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("Request to delete operation with id {}", id);

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
