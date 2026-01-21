package com.kayky.domain.operation;

import com.kayky.commons.FileUtils;
import com.kayky.commons.OperationUtils;
import com.kayky.commons.PageUtils;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.operation.request.OperationBaseRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Operation Controller")
@WebMvcTest(controllers = OperationController.class)
class OperationControllerTest {

    private static final String BASE_URI = "/v1/operation";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService service;

    private String validCreateRequest;
    private String validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = FileUtils.readResourceFile("operation/controller/post/request/request-create-operation-201.json");
        validUpdateRequest = FileUtils.readResourceFile("operation/controller/put/request/request-update-operation-200.json");
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
    @DisplayName("GET /v1/operation/{id} - Should return 200 when operation exists")
    void getOperation_shouldReturn200_whenExists() throws Exception {
        var operationId = EXISTING_ID;
        var savedOperation = OperationUtils.savedOperation();
        var response = OperationUtils.asBaseResponse(savedOperation);

        when(service.findById(operationId)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, operationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("operation/controller/get/operation-by-id-200.json")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(operationId);
    }

    @Test
    @DisplayName("GET /v1/operation/{id} - Should return 404 when operation does not exist")
    void getOperation_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = OPERATION_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("operation/controller/get/operation-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/operation - Should return paged result when operations exist")
    void getOperations_shouldReturnPagedResults_whenOperationsExist() throws Exception {
        var operationList = OperationUtils.operationDetailsResponseList();
        var operationPage = PageUtils.toPage(operationList);
        var pageResponse = PageUtils.pageResponse(operationPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("operation/controller/get/all-paged-operations-200.json")))
                .andExpect(jsonPath("$.content").isArray());

        verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/operation - Should return 201 when request is valid")
    void createOperation_shouldReturn201_whenRequestIsValid() throws Exception {
        var savedOperation = OperationUtils.savedOperation();
        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        when(service.save(any(OperationBaseRequest.class))).thenReturn(expectedResponse);

        performPostRequest(validCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/v1/operation/" + EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("operation/controller/post/response/response-created-operation-201.json")));

        verify(service).save(any(OperationBaseRequest.class));
    }


    @Test
    @DisplayName("POST /v1/operation - Should return 404 when patient does not exist")
    void createOperation_shouldReturn404_WhenPatientDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException(PATIENT_NOT_FOUND))
                .when(service).save(any(OperationBaseRequest.class));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Patient")));

        verify(service).save(any(OperationBaseRequest.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("POST /v1/operation - Should return 404 when doctor does not exist")
    void createOperation_shouldReturn404_WhenDoctorDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException(DOCTOR_NOT_FOUND))
                .when(service).save(any(OperationBaseRequest.class));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Doctor")));

        verify(service).save(any(OperationBaseRequest.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("PUT /v1/operation/{id} - Should return 200 when request is valid")
    void updateOperation_shouldReturn200_whenRequestIsValid() throws Exception {
        var updatedOperation = OperationUtils.updatedOperation();
        var operationId = updatedOperation.getId();
        var expectedResponse = OperationUtils.asBaseResponse(updatedOperation);

        when(service.update(any(OperationBaseRequest.class), eq(operationId)))
                .thenReturn(expectedResponse);

        performPutRequest(operationId, validUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("operation/controller/put/response/response-updated-operation.json")));

        verify(service).update(any(OperationBaseRequest.class), eq(operationId));
    }


    @Test
    @DisplayName("PUT /v1/operation/{id} - Should return 404 when patient does not exist")
    void updateOperation_shouldReturn404_WhenPatientDoesNotExist() throws Exception {
        Long operationId = EXISTING_ID;

        doThrow(new ResourceNotFoundException(PATIENT_NOT_FOUND))
                .when(service).update(any(OperationBaseRequest.class), eq(operationId));

        performPutRequest(operationId, validUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Patient")));

        verify(service).update(any(OperationBaseRequest.class), eq(operationId));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("PUT /v1/operation/{id} - Should return 404 when doctor does not exist")
    void updateOperation_shouldReturn404_WhenDoctorDoesNotExist() throws Exception {
        Long operationId = EXISTING_ID;

        doThrow(new ResourceNotFoundException(DOCTOR_NOT_FOUND))
                .when(service).update(any(OperationBaseRequest.class), eq(operationId));

        performPutRequest(operationId, validUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Doctor")));

        verify(service).update(any(OperationBaseRequest.class), eq(operationId));
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("DELETE /v1/operation/{id} - Should return 204 No Content when operation is deleted successfully")
    void delete_ShouldReturn204NoContent_WhenOperationExists() throws Exception {
        doNothing().when(service).delete(EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, EXISTING_ID))
                .andExpect(status().isNoContent());

        verify(service).delete(EXISTING_ID);
    }

    @Test
    @DisplayName("DELETE /v1/operation/{id} - Should return 404 Not Found when operation does not exist")
    void delete_ShouldReturn404NotFound_WhenOperationDoesNotExist() throws Exception {
        var expectedErrorMessage = OPERATION_NOT_FOUND;

        doThrow(new ResourceNotFoundException(expectedErrorMessage))
                .when(service).delete(NON_EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).delete(NON_EXISTING_ID);
    }
}