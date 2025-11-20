package com.kayky.commons;

import com.kayky.domain.receipt.Receipt;
import com.kayky.domain.receipt.response.ReceiptBaseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReceiptUtils {

    public static Receipt savedReceiptWithIssuedAt(Long id, LocalDateTime issuedAt) {
        return Receipt.builder()
                .id(id)
                .payment(PaymentUtils.savedPayment(1L))
                .cashier(CashierUtils.savedCashier(1L))
                .patient(PatientUtils.savedPatient(1L))
                .issuedAt(issuedAt)
                .receiptNumber("RCT-ABC12345")
                .totalAmount(new BigDecimal("150.00"))
                .build();

    }

    public static ReceiptBaseResponse asBaseResponse(Receipt receipt) {
        return ReceiptBaseResponse.builder()
                .id(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .cashierName(receipt.getCashier().getFirstName())
                .patientName(receipt.getPatient().getFirstName())
                .issuedAt(receipt.getIssuedAt())
                .totalAmount(receipt.getTotalAmount())
                .build();
    }

}
