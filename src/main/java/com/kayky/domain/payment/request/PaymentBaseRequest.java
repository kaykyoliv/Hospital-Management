package com.kayky.domain.payment.request;

import com.kayky.domain.payment.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentBaseRequest(
        Long patientId,
        Long cashierId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {}