package com.kayky.domain.payment;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.PaymentUtils;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.payment.request.PaymentBaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Payment controller")
@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService service;

    private static final String BASE_URI = "/v1/payment";
    private static final String PATH_ID = BASE_URI + "/{id}";

    private String validCreateRequest;
    
    @BeforeEach
    void setUp() {
        validCreateRequest = FileUtils.readResourceFile("payment/controller/post/request/request-create-payment-201.json");
    }
    
    private String loadExpectedJson(String resourcePath) {
        return FileUtils.readResourceFile(resourcePath);
    }

    private String urlForPatientPayments(Long patientId) {
        return BASE_URI + "/patients/" + patientId + "/payments";
    }

    private ResultActions performPostRequest(String jsonContent) throws Exception {
        return mockMvc.perform(post(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent));
    }


        @Test
        @DisplayName("GET v1/payment/{id} - Should return 200 when payment exists")
        void getPayment_shouldReturn200_whenExists() throws Exception{
        var paymentId = EXISTING_ID;
        var savedPayment = PaymentUtils.savedPayment(paymentId);
        var response = PaymentUtils.asBaseResponse(savedPayment);
        
        when(service.findById(paymentId)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, paymentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("payment/controller/get/payment-by-id-200.json")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/payment/{id} - Should return 404 when payment does not exist")
    void getPayment_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = PAYMENT_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("payment/controller/get/payment-by-id-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/payment - Should return paged result when payments exist")
    void getPayments_shouldReturnPagedResults_whenPaymentsExist() throws Exception {
        var paymentList = PaymentUtils.baseResponseList();
        var paymentPage = PageUtils.toPage(paymentList);
        var pagedPayment = PageUtils.pageResponse(paymentPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pagedPayment);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("payment/controller/get/all-paged-payments-200.json")));


        verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/payment/patients/{patientId}/payments - Should return list of payments when patient exists")
    void findByPatient_shouldReturnPaymentList_whenPatientExists() throws Exception{

        var patientId = EXISTING_ID;
        var patientPayments  = PaymentUtils.paymentsForPatient(patientId);

        when(service.findByPatient(patientId)).thenReturn(patientPayments);

        mockMvc.perform(get(urlForPatientPayments(patientId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("payment/controller/get/payments-by-patient-200.json")));

        verify(service).findByPatient(patientId);
    }

    @Test
    @DisplayName("GET /v1/payment/patients/{patientId}/payments - Should return 404 when patient is not found")
    void findByPatient_shouldReturn404_whenPatientDoesNotExist() throws Exception {
        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.findByPatient(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(urlForPatientPayments(NON_EXISTING_ID))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("payment/controller/get/payments-by-patient-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findByPatient(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("POST /v1/payment - Should return 201 when request is valid")
    void createPayment_ShouldReturn201_whenRequestIsValid() throws Exception {
        var savedPayment = PaymentUtils.savedPayment(EXISTING_ID);
        var response = PaymentUtils.asBaseResponse(savedPayment);

        when(service.save(any(PaymentBaseRequest.class))).thenReturn(response);

        performPostRequest(validCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("payment/controller/post/response/response-created-payment-200.json")));

        verify(service).save(any(PaymentBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/payment - Should return 404 when patient does not exist")
    void createPayment_ShouldReturn404_whenPatientDoesNotExist() throws Exception {
        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.save(any(PaymentBaseRequest.class)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

    @Test
    @DisplayName("POST /v1/payment - Should return 404 when cashier does not exist")
    void createPayment_shouldReturn404_whenCashierDoesNotExist() throws Exception {
        var expectedErrorMessage = CASHIER_NOT_FOUND;

        when(service.save(any(PaymentBaseRequest.class)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

    @Test
    @DisplayName("POST /v1/payment - Should return 422 when request is invalid")
    void createPayment_shouldReturn422_whenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("payment/controller/post/request/request-create-payment-invalid-422.json");

        performPostRequest(invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json(loadExpectedJson("payment/controller/post/response/validation-error-422.json")));
    }
}