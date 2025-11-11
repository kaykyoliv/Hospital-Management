package com.kayky.domain.payment.response;

import com.kayky.domain.payment.enums.PaymentMethod;
import com.kayky.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentBaseResponse(
        Long id,
        Long patientId,
        String patientName,
        Long cashierId,
        String cashierName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        LocalDateTime paymentDate
) {}
