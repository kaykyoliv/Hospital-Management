package com.kayky.domain.payment.request;

import com.kayky.domain.payment.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentBaseRequest(
        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be greater than zero")
        Long patientId,

        @NotNull(message = "Cashier ID is required")
        @Positive(message = "Cashier ID must be greater than zero")
        Long cashierId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true,
                message = "Amount must be at least 0.01")
        @Digits(integer = 10, fraction = 2,
                message = "Amount must have at most 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {}