package com.kayky.domain.receipt;

import com.kayky.commons.PaymentUtils;
import com.kayky.commons.ReceiptUtils;
import com.kayky.core.exception.ReceiptAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.payment.PaymentRepository;
import com.kayky.domain.receipt.generator.ReceiptNumberGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

@DisplayName("Receipt Service - Unit Tests")
@ExtendWith(MockitoExtension.class)
public class ReceiptServiceTest {

    private ReceiptService service;

    private final ReceiptMapper mapper = Mappers.getMapper(ReceiptMapper.class);

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReceiptNumberGenerator numberGenerator;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 1, 10, 12, 0).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );

        service = new ReceiptService(receiptRepository, paymentRepository, mapper, numberGenerator, fixedClock);
    }

    @Test
    @DisplayName("Should emit receipt successfully when payment exists and no receipt yet")
    void emit_shouldReturnReceipt_whenPaymentExistsAndReceiptDoesNotExist() {
        var now = LocalDateTime.now(fixedClock);

        var savedReceipt = ReceiptUtils.savedReceiptWithIssuedAt(EXISTING_ID, now);
        var payment = PaymentUtils.savedPayment(EXISTING_ID);
        var expectedResponse = ReceiptUtils.asBaseResponse(savedReceipt);

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(receiptRepository.existsByPaymentId(payment.getId())).thenReturn(false);
        when(numberGenerator.generate()).thenReturn("RCT-ABC12345");

        when(receiptRepository.save(any())).thenAnswer(invocation -> {
            Receipt r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        var result = service.emit(payment.getId());

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when payment does not exist")
    void emit_shouldThrowNotFound_whenPaymentDoesNotExist() {
        when(paymentRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.emit(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PAYMENT_NOT_FOUND);

        verify(paymentRepository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("Should throw ReceiptAlreadyExistsException when receipt already exists for payment")
    void emit_shouldThrowAlreadyExists_whenReceiptExistsForPayment() {
        var payment = PaymentUtils.savedPayment(EXISTING_ID);

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(receiptRepository.existsByPaymentId(payment.getId())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.emit(EXISTING_ID))
                .isInstanceOf(ReceiptAlreadyExistsException.class)
                .hasMessage(RECEIPT_ALREADY_EXISTS.formatted(payment.getId()));

        verify(paymentRepository).findById(EXISTING_ID);
    }

}
