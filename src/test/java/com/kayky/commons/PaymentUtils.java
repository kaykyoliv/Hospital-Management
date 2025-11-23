package com.kayky.commons;

import com.kayky.domain.payment.Payment;
import com.kayky.domain.payment.enums.PaymentMethod;
import com.kayky.domain.payment.enums.PaymentStatus;
import com.kayky.domain.payment.request.PaymentBaseRequest;
import com.kayky.domain.payment.response.PaymentBaseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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


    public static PaymentBaseResponse asBaseResponse(Payment payment) {
        return PaymentBaseResponse.builder()
                .id(payment.getId())
                .patientId(payment.getPatient().getId())
                .patientName(payment.getPatient().getFirstName() + " " + payment.getPatient().getLastName())
                .cashierId(payment.getCashier().getId())
                .cashierName(payment.getCashier().getFirstName() + " " + payment.getCashier().getLastName())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    public static List<Payment> paymentList() {
        return List.of(
                Payment.builder()
                        .id(1L)
                        .patient(PatientUtils.savedPatient(1L))
                        .cashier(CashierUtils.savedCashier(1L))
                        .amount(new BigDecimal("150.00"))
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .status(PaymentStatus.PAID)
                        .paymentDate(LocalDateTime.of(2025, 1, 10, 15, 0))
                        .build(),

                Payment.builder()
                        .id(2L)
                        .patient(PatientUtils.savedPatient(2L))
                        .cashier(CashierUtils.savedCashier(1L))
                        .amount(new BigDecimal("320.50"))
                        .paymentMethod(PaymentMethod.DEBIT_CARD)
                        .status(PaymentStatus.PENDING)
                        .paymentDate(LocalDateTime.of(2025, 2, 5, 11, 30))
                        .build(),

                Payment.builder()
                        .id(3L)
                        .patient(PatientUtils.savedPatient(3L))
                        .cashier(CashierUtils.savedCashier(2L))
                        .amount(new BigDecimal("89.99"))
                        .paymentMethod(PaymentMethod.CASH)
                        .status(PaymentStatus.CANCELLED)
                        .paymentDate(LocalDateTime.of(2025, 3, 1, 9, 45))
                        .build()
        );
    }

    public static List<PaymentBaseResponse> baseResponseList(){
        return paymentList().stream().map(PaymentUtils::asBaseResponse).toList();
    }

    public static List<PaymentBaseResponse> paymentsForPatient(Long patientId) {
        return baseResponseList().stream()
                .filter(p -> p.patientId().equals(patientId))
                .toList();
    }


    public static PaymentBaseRequest asBaseRequest() {
        return PaymentBaseRequest.builder()
                .patientId(1L)
                .cashierId(1L)
                .amount(new BigDecimal("99.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

    }


}
