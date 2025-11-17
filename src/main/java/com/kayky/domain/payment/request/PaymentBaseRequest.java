package com.kayky.domain.payment.request;

import com.kayky.domain.payment.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;


@Schema(
        description = "Request payload for creating a new payment"
)
public record PaymentBaseRequest(


        @Schema(
                description = "Identifier of the patient who is making the payment",
                example = "12"
        )
        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be greater than zero")
        Long patientId,

        @Schema(
                description = "Identifier of the cashier responsible for registering the payment",
                example = "7"
        )
        @NotNull(message = "Cashier ID is required")
        @Positive(message = "Cashier ID must be greater than zero")
        Long cashierId,

        @Schema(
                description = "Amount paid by the patient",
                example = "150.75"
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true,
                message = "Amount must be at least 0.01")
        @Digits(integer = 10, fraction = 2,
                message = "Amount must have at most 2 decimal places")
        BigDecimal amount,

        @Schema(
                description = "Method of payment used",
                example = "CREDIT_CARD"
        )
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}