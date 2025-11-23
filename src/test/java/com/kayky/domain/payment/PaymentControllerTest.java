package com.kayky.domain.payment;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.PaymentUtils;
import com.kayky.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService service;

    private static final String BASE_URI = "/v1/payment";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @BeforeEach
    void setUp() {
    }
    
    private String loadExpectedJson(String resourcePath) {
        return FileUtils.readResourceFile(resourcePath);
    }

    @Test
    @DisplayName("GET /v1/payment/{id} - Should return 200 with payment data when payment exists")
    void findById_ShouldReturnPaymentGetResponse_WhenPaymentExists() throws Exception {
        var paymentId = EXISTING_ID;
        var savedPayment = PaymentUtils.savedPayment(paymentId);
        var response = PaymentUtils.asBaseResponse(savedPayment);
        
        when(service.findById(paymentId)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, paymentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("payment/get/payment-by-id-200.json")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/payment/{id} - Should return 404 when payment is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenPaymentDoesNotExist() throws Exception {
        var expectedErrorMessage = PAYMENT_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("payment/get/payment-by-id-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/payment - Should return page with payments")
    void findAll_ShouldReturnPaymentPageResponse_WhenPaymentExist() throws Exception {
        var paymentList = PaymentUtils.baseResponseList();
        var paymentPage = PageUtils.toPage(paymentList);
        var pagedPayment = PageUtils.pageResponse(paymentPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pagedPayment);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("payment/get/all-paged-payments-200.json")));


        verify(service).findAll(any(Pageable.class));
    }
}