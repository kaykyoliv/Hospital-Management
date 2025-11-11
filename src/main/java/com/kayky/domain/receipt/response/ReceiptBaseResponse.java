package com.kayky.domain.receipt.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptBaseResponse(
        Long id,
        String receiptNumber,
        String cashierName,
        String patientName,
        LocalDateTime issuedAt,
        BigDecimal totalAmount
) {}
