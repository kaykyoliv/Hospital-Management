package com.kayky.domain.receipt;

import com.kayky.domain.receipt.response.ReceiptBaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/{paymentId}/receipt")
    public ResponseEntity<ReceiptBaseResponse> emit(@PathVariable Long paymentId) {
        log.debug("Request to emit receipt for payment {}", paymentId);

        var response = receiptService.emit(paymentId);
        return ResponseEntity.ok(response);
    }
}