package com.kayky.domain.receipt;

import com.kayky.commons.FileUtils;
import com.kayky.commons.ReceiptUtils;
import com.kayky.core.exception.ReceiptAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.kayky.commons.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentReceiptController.class)
class PaymentReceiptControllerTest {

    private static final String BASE_URI = "/v1/payment/{paymentId}/receipt";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReceiptService service;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 1, 10, 12, 0).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );
    }

    private String loadExpectedJson(String resourcePath) {
        return FileUtils.readResourceFile(resourcePath);
    }

    private ResultActions performPostReceipt(Long paymentId) throws Exception {
        return mockMvc.perform(post(BASE_URI, paymentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /v1/payment/{paymentId}/receipt - Should return 200 OK when receipt is emitted successfully")
    void emit_ShouldReturn200Ok_WhenReceiptEmittedSuccessfully() throws Exception {
        var paymentId = EXISTING_ID;
        var savedReceipt = ReceiptUtils.savedReceiptWithIssuedAt(paymentId, LocalDateTime.now(fixedClock));
        var response = ReceiptUtils.asBaseResponse(savedReceipt);

        when(service.emit(paymentId)).thenReturn(response);

        performPostReceipt(paymentId)
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("payment/receipt/response-emit-receipt-200.json")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).emit(paymentId);
    }

    @Test
    @DisplayName("POST /v1/payment/{paymentId}/receipt - Should return 404 Not Found when payment does not exist")
    void emit_ShouldReturn404NotFound_WhenPaymentNotFound() throws Exception{
        var paymentId = NON_EXISTING_ID;

        var expectedErrorMessage = PAYMENT_NOT_FOUND;

        when(service.emit(paymentId)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPostReceipt(paymentId)
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("payment/receipt/response-emit-receipt-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).emit(paymentId);
    }

    @Test
    @DisplayName("POST /v1/payment/{paymentId}/receipt - Should return 409 Conflict when receipt already exists for payment")
    void emit_ShouldReturn409Conflict_WhenReceiptAlreadyExists() throws Exception {
        var paymentId = EXISTING_ID;

        when(service.emit(paymentId)).thenThrow(new ReceiptAlreadyExistsException(paymentId));

        performPostReceipt(paymentId)
                .andExpect(status().isConflict())
                .andExpect(content().json(loadExpectedJson("payment/receipt/response-emit-receipt-409.json")))
                .andExpect(jsonPath("$.error").value(RECEIPT_ALREADY_EXISTS.formatted(paymentId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).emit(paymentId);
    }
}