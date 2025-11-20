package com.kayky.commons;

import com.kayky.domain.payment.Payment;
import com.kayky.domain.payment.enums.PaymentMethod;
import com.kayky.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentUtils {

    public static Payment savedPayment(Long id) {
        return Payment.builder()
                .id(id)
                .patient(PatientUtils.savedPatient(1L))
                .cashier(CashierUtils.savedCashier(1L))
                .amount(new BigDecimal("150.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PAID)
                .paymentDate(LocalDateTime.of(2025, 1, 10, 15, 0))
                .build();
    }

}
