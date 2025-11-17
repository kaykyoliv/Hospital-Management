package com.kayky.domain.receipt;

import com.kayky.core.exception.ApiError;
import com.kayky.domain.receipt.response.ReceiptBaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentReceiptController {

    private final ReceiptService receiptService;

    @Operation(
            summary = "Emit receipt for a payment",
            description = "Generates a receipt for an existing payment if one does not already exist."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Receipt emitted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReceiptBaseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A receipt has already been issued for this payment",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/{paymentId}/receipt")
    public ResponseEntity<ReceiptBaseResponse> emit(@PathVariable Long paymentId) {
        log.debug("Request to emit receipt for payment {}", paymentId);

        var response = receiptService.emit(paymentId);
        return ResponseEntity.ok(response);
    }
}