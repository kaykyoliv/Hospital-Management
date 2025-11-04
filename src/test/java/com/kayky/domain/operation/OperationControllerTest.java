package com.kayky.domain.operation;

import com.kayky.commons.FileUtils;
import com.kayky.commons.OperationUtils;
import com.kayky.commons.PageUtils;
import com.kayky.domain.operation.request.OperationBaseRequest;
import com.kayky.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OperationController.class)
class OperationControllerTest {

    private static final String BASE_URI = "/v1/operation";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService service;


    @Test
    @DisplayName("GET /v1/operation/{id} - Should return 200 with operation data when operation exists")
    void findById_ShouldReturnOperationGetResponse_WhenOperationExists() throws Exception {

        var savedOperation = OperationUtils.savedOperation();
        var response = OperationUtils.asBaseResponse(savedOperation);

        BDDMockito.when(service.findById(EXISTING_ID)).thenReturn(response);

        var expectedResponse = FileUtils.readResourceFile("operation/get/operation-by-id-200.json");

        mockMvc.perform(get(PATH_ID, response.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        BDDMockito.verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/operation/{id} - Should return 404 when operation is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenOperationDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("operation/get/operation-not-found-404.json");

        var expectedErrorMessage = OPERATION_NOT_FOUND;

        BDDMockito.when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/operation - Should return page with operations")
    void findAll_ShouldReturnOperationPageResponse_WhenOperationsExist() throws Exception {
        var operationList = OperationUtils.operationDetailsResponseList();
        var operationPage = PageUtils.toPage(operationList);
        var pageResponse = PageUtils.pageResponse(operationPage);

        var expectedJsonResponse = FileUtils.readResourceFile("operation/get/all-paged-operations-200.json");

        BDDMockito.when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/operation - Should return 201 Created when operation is saved successfully")
    void save_ShouldReturn201Created_WhenRequestIsValid() throws Exception {
        var request = FileUtils.readResourceFile("operation/post/request-create-operation-201.json");
        var expectedJsonResponse = FileUtils.readResourceFile("operation/post/response-created-operation-200.json");

        var savedOperation = OperationUtils.savedOperation();
        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        BDDMockito.when(service.save(any(OperationBaseRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).save(any(OperationBaseRequest.class));
    }


    @ParameterizedTest(name = "POST /v1/operation: should return 404 when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void save_ShouldReturn404_WhenNonExistingUser(String nonExistingType) throws Exception {
        var jsonRequest = FileUtils.readResourceFile("operation/post/request-create-operation-201.json");
        var request = OperationUtils.asBaseRequest();

        var nonExistingId = nonExistingType.equals("Doctor") ?
                request.getDoctor().getId() : request.getPatient().getId();
        var nonExistingName = nonExistingType.equals("Doctor") ?
                request.getDoctor().getFirstName() : request.getPatient().getFirstName();

        doThrow(new ResourceNotFoundException(
                USER_NOT_FOUND_SAVE_OPERATION.formatted(nonExistingName, nonExistingId)))
                .when(service).save(any(OperationBaseRequest.class));

        mockMvc.perform(post(BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value(USER_NOT_FOUND_SAVE_OPERATION.formatted(nonExistingName, nonExistingId)));
    }

    @Test
    @DisplayName("PUT /v1/operation/{id} - Should return 200 OK when operation is updated successfully")
    void update_ShouldReturn200OK_WhenRequestIsValid() throws Exception {
        var request = FileUtils.readResourceFile("operation/put/request-update-operation.json");
        var expectedJsonResponse = FileUtils.readResourceFile("operation/put/response-updated-operation.json");

        var updatedOperation = OperationUtils.updatedOperation();
        var expectedResponse = OperationUtils.asBaseResponse(updatedOperation);

        BDDMockito.when(service.update(any(OperationBaseRequest.class), eq(EXISTING_ID)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).update(any(OperationBaseRequest.class), eq(EXISTING_ID));
    }


    @ParameterizedTest(name = "PUT /v1/operation/id - Should return 404 when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void update_ShouldReturn404_WhenNonExistingUser(String nonExistingType) throws Exception {
        var jsonRequest = FileUtils.readResourceFile("operation/put/request-update-operation.json");
        var request = OperationUtils.asBaseRequest();

        var nonExistingId = nonExistingType.equals("Doctor") ?
                request.getDoctor().getId() : request.getPatient().getId();
        var nonExistingName = nonExistingType.equals("Doctor") ?
                request.getDoctor().getFirstName() : request.getPatient().getFirstName();

        doThrow(new ResourceNotFoundException(
                USER_NOT_FOUND_SAVE_OPERATION.formatted(nonExistingName, nonExistingId)))
                .when(service).update(any(OperationBaseRequest.class), eq(NON_EXISTING_ID));

        mockMvc.perform(put(PATH_ID, NON_EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value(USER_NOT_FOUND_SAVE_OPERATION.formatted(nonExistingName, nonExistingId)));
    }

    @Test
    @DisplayName("DELETE /v1/operation/{id} - Should return 204 No Content when operation is deleted successfully")
    void delete_ShouldReturn204NoContent_WhenOperationExists() throws Exception {
        BDDMockito.doNothing().when(service).delete(EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        BDDMockito.verify(service).delete(EXISTING_ID);
    }

    @Test
    @DisplayName("DELETE /v1/operation/{id} - Should return 404 Not Found when operation does not exist")
    void delete_ShouldReturn404NotFound_WhenOperationDoesNotExist() throws Exception {
        var expectedErrorMessage = OPERATION_NOT_FOUND;

        BDDMockito.doThrow(new ResourceNotFoundException(expectedErrorMessage))
                .when(service).delete(NON_EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        BDDMockito.verify(service).delete(NON_EXISTING_ID);
    }

    private static Stream<String> provideNonExistingTypes() {
        return Stream.of("Doctor", "Patient");

    }
}