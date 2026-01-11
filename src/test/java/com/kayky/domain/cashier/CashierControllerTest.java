package com.kayky.domain.cashier;

import com.kayky.commons.CashierUtils;
import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.cashier.request.CashierBaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.kayky.commons.TestConstants.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Cashier Controller")
@WebMvcTest(controllers = CashierController.class)
class CashierControllerTest {

    private static final String BASE_URI = "/v1/cashier";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CashierService service;

    private String validCreateRequest;
    private String validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = FileUtils.readResourceFile("cashier/controller/post/request/request-create-cashier-201.json");
        validUpdateRequest = FileUtils.readResourceFile("cashier/controller/put/request/request-update-cashier-200.json");
    }

    private String loadExpectedJson(String resourcePath) {
        return FileUtils.readResourceFile(resourcePath);
    }

    private ResultActions performPostRequest(String jsonContent) throws Exception {
        return mockMvc.perform(post(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent));
    }

    private ResultActions performPutRequest(Long id, String jsonContent) throws Exception {
        return mockMvc.perform(put(PATH_ID, id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent));
    }

    @Test
    @DisplayName("GET /v1/cashier/{id} - Should return 200 when cashier exists")
    void getCashier_shouldReturn200_whenExists() throws Exception {
        var cashierId = EXISTING_ID;
        var savedCashier = CashierUtils.savedCashier(cashierId);

        var response = CashierUtils.asBaseResponse(savedCashier);

        when(service.findById(cashierId)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, cashierId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("cashier/controller/get/cashier-by-id-200.json")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/cashier/{id} - Should return 404 when cashier does not exist")
    void getCashier_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = CASHIER_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("cashier/controller/get/cashier-by-id-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/cashier - Should return paged result when cashiers exist")
    void getCashiers_shouldReturnPagedResults_whenCashiersExist() throws Exception {
        var cashierList = CashierUtils.baseResponseList();
        var cashierPage = PageUtils.toPage(cashierList);
        var pagedCashier = PageUtils.pageResponse(cashierPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pagedCashier);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("cashier/controller/get/all-paged-cashiers-200.json")))
                .andExpect(jsonPath("$.content").isArray());

        verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/cashier - Should return 201 when request is valid")
    void createCashier_shouldReturn201_whenRequestIsValid() throws Exception {
        var savedCashier = CashierUtils.savedCashier(EXISTING_ID);
        var response = CashierUtils.asBaseResponse(savedCashier);

        when(service.save(any(CashierBaseRequest.class))).thenReturn(response);

        performPostRequest(validCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/v1/cashier/" + EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("cashier/controller/post/response/response-created-cashier-200.json")));

        verify(service).save(any(CashierBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/cashier - Should return 400 when email already exists")
    void createCashier_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        var request = CashierUtils.asBaseRequest();

        var expectedErrorMessage = EMAIL_ALREADY_EXISTS.formatted(request.email());

        when(service.save(any(CashierBaseRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("cashier/controller/post/response/response-email-already-exists-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(CashierBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/cashier - Should return 422 when request is invalid")
    void createCashier_shouldReturn422_whenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("cashier/controller/post/request/request-create-cashier-invalid-422.json");

        performPostRequest(invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json(loadExpectedJson("cashier/controller/post/response/response-validation-error-422.json")));
    }

    @Test
    @DisplayName("PUT /v1/cashier/{id} - Should return 200 when request is valid")
    void updateCashier_shouldReturn200_whenRequestIsValid() throws Exception {
        var updatedCashier = CashierUtils.updatedCashier();
        var response = CashierUtils.asBaseResponse(updatedCashier);

        when(service.update(any(CashierBaseRequest.class), eq(response.id()))).thenReturn(response);

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("cashier/controller/put/response/response-updated-cashier-200.json")));

        verify(service).update(any(CashierBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/cashier/{id} - Should return 404 when cashier does not exist")
    void updateCashier_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = CASHIER_NOT_FOUND;

        when(service.update(any(CashierBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(NON_EXISTING_ID, validUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(loadExpectedJson("cashier/controller/put/response/response-cashier-not-found-404.json")));

        verify(service).update(any(CashierBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/cashier/{id} - Should return 400 when email already exists")
    void updateCashier_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        var request = CashierUtils.asBaseRequest();

        var expectedErrorMessage = EMAIL_ALREADY_EXISTS.formatted(request.email());

        when(service.update(any(CashierBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("cashier/controller/put/response/response-email-already-exists-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(CashierBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/cashier/{id} - Should return 422 when request is invalid")
    void updateCashier_shouldReturn422_whenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("cashier/controller/put/request/request-update-cashier-invalid-422.json");

        performPutRequest(EXISTING_ID, invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("cashier/controller/put/response/response-validation-error-422.json")));

        verify(service, never()).update(any(), anyLong());
    }
}