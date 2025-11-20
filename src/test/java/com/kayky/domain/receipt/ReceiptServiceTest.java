package com.kayky.domain.receipt;

import com.kayky.commons.PaymentUtils;
import com.kayky.commons.ReceiptUtils;
import com.kayky.domain.payment.PaymentRepository;
import com.kayky.domain.receipt.generator.ReceiptNumberGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.util.Optional;

import static com.kayky.commons.TestConstants.EXISTING_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;


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
    void shouldEmitReceiptSuccessfully() {
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
}
