package com.kayky.domain.cashier;

import com.kayky.commons.CashierUtils;
import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CashierController.class)
class CashierControllerTest {

    private static final String BASE_URI = "/v1/cashier";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CashierService service;


    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("GET /v1/cashier/{id} - Should return 200 with cashier data when cashier exists")
    void findById_ShouldReturnCashierGetResponse_WhenCashierExists() throws Exception {
        var cashierId = EXISTING_ID;
        var savedCashier = CashierUtils.savedCashier(cashierId);

        var response = CashierUtils.asBaseResponse(savedCashier);
        var expectedJsonResponse = FileUtils.readResourceFile("cashier/get/cashier-by-id-200.json");

        when(service.findById(cashierId)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, cashierId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/cashier/{id} - Should return 404 when cashier is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenCashierDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("cashier/get/cashier-by-id-404.json");
        var expectedErrorMessage = CASHIER_NOT_FOUND;
        
        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedJsonResponse))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/cashier - Should return page with cashiers")
    void findAll_ShouldReturnCashierPageResponse_WhenCashierExist() throws Exception {
        var cashierList = CashierUtils.baseResponseList();
        var cashierPage = PageUtils.toPage(cashierList);
        var pagedCashier = PageUtils.pageResponse(cashierPage);

        var expectedJsonResponse = FileUtils.readResourceFile("cashier/get/all-paged-cashiers-200.json");

        when(service.findAll(any(Pageable.class))).thenReturn(pagedCashier);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).findAll(any(Pageable.class));
    }
}