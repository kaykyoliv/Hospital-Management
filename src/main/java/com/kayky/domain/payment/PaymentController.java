package com.kayky.domain.payment;

import com.kayky.core.exception.ApiError;
import com.kayky.core.exception.ValidationError;
import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.payment.request.PaymentBaseRequest;
import com.kayky.domain.payment.response.PaymentBaseResponse;
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
import java.util.List;

@RestController
@RequestMapping(value = "v1/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Operations related to payment management")
public class PaymentController {

    private final PaymentService service;

    @Operation(
            summary = "Find payment by ID",
            description = "Retrieves payment by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentBaseResponse> findById(@PathVariable Long id) {
        log.debug("Request to find payments by id {}", id);

        var response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List all payments",
            description = "Returns a paginated list of all registered payments"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paginated list of payments returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageResponse.class))
    )
    @GetMapping
    public PageResponse<PaymentBaseResponse> findAllPaged(Pageable pageable) {
        log.debug("Request received to list all payment");
        return service.findAll(pageable);
    }

    @Operation(
            summary = "Find all payments by patient ID",
            description = "Returns a list of all payments associated with the given patient"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments found successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/patients/{patientId}/payments")
    public ResponseEntity<List<PaymentBaseResponse>> findByPatient(@PathVariable Long patientId) {
        log.debug("Request to find all payments by patient id {}", patientId);

        var response = service.findByPatient(patientId);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Register a new payment",
            description = "Creates a new payment and its ID and details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cashier or Patient not found",
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
    public ResponseEntity<PaymentBaseResponse> save(@Valid @RequestBody PaymentBaseRequest request) {
        log.debug("Request to create new payment");

        var response = service.save(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }
}
