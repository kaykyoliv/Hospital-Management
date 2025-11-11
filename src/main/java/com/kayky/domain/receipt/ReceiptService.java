package com.kayky.domain.receipt;

import com.kayky.core.exception.ReceiptAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.payment.PaymentRepository;
import com.kayky.domain.receipt.response.ReceiptBaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptMapper receiptMapper;

    public ReceiptBaseResponse emit(Long paymentId){
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if(receiptRepository.existsByPaymentId(paymentId)){
            throw new ReceiptAlreadyExistsException(paymentId);
        }

        var receipt = Receipt.builder()
                .payment(payment)
                .cashier(payment.getCashier())
                .patient(payment.getPatient())
                .issuedAt(LocalDateTime.now())
                .receiptNumber(generateReceiptNumber())
                .totalAmount(payment.getAmount())
                .build();

        receiptRepository.save(receipt);

        return receiptMapper.toReceiptBaseResponse(receipt);
    }

    private String generateReceiptNumber() {
        return "RCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
