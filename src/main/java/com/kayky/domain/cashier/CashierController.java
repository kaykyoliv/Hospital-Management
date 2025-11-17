package com.kayky.domain.cashier;

import com.kayky.core.exception.ApiError;
import com.kayky.core.exception.ValidationError;
import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.cashier.request.CashierBaseRequest;
import com.kayky.domain.cashier.response.CashierBaseResponse;
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
@RequestMapping(value = "v1/cashier")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cashier", description = "Operations related to cashier management")
public class CashierController {

    private final CashierService service;

    @Operation(
            summary = "Find cashier by ID",
            description = "Retrieves cashier by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cashier found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CashierBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cashier not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<CashierBaseResponse> findById(@PathVariable Long id) {
        log.debug("Request to find cashier by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List all cashiers",
            description = "Returns a paginated list off all registered cashiers"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paginated list of cashiers returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageResponse.class))
    )
    @GetMapping
    public PageResponse<CashierBaseResponse> findAllPaged(Pageable pageable) {
        log.debug("Request received to list all cashiers");
        return service.findAll(pageable);
    }

    @Operation(
            summary = "Register a new cashier",
            description = "Creates a new cashier and its ID and details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cashier created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CashierBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already exists",
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
    public ResponseEntity<CashierBaseResponse> save(@Valid @RequestBody CashierBaseRequest request) {
        log.debug("Request to create new cashier");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Update cashier information",
            description = "Updates a cashier's details by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cashier updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CashierBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cashier not found",
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
    public ResponseEntity<CashierBaseResponse> update(@Valid @RequestBody CashierBaseRequest request, @PathVariable Long id) {
        log.debug("Request to update cashier with id {}", id);

        var response = service.update(request, id);

        return ResponseEntity.ok(response);
    }
}
