package com.kayky.domain.receipt;

import com.kayky.core.exception.ReceiptAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.payment.PaymentRepository;
import com.kayky.domain.receipt.generator.ReceiptNumberGenerator;
import com.kayky.domain.receipt.response.ReceiptBaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptMapper receiptMapper;
    private final ReceiptNumberGenerator receiptNumberGenerator;
    private final Clock clock;

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
                .issuedAt(LocalDateTime.now(clock))
                .receiptNumber(receiptNumberGenerator.generate())
                .totalAmount(payment.getAmount())
                .build();

        var savedReceipt = receiptRepository.save(receipt);

        return receiptMapper.toReceiptBaseResponse(savedReceipt);
    }

}
