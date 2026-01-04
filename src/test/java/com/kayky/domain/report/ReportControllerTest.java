package com.kayky.domain.report;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.ReportUtils;
import com.kayky.core.exception.OperationMismatchException;
import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.report.request.ReportBaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportController.class)
class ReportControllerTest {

    private static final String BASE_URI = "/v1/report";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService service;

    String validUpdateRequest;
    String validCreateRequest;

    @BeforeEach
    void setup() {
        validUpdateRequest = FileUtils.readResourceFile("report/controller/put/request-update-report-200.json");
        validCreateRequest = FileUtils.readResourceFile("report/controller/post/request-create-report-201.json");
    }

    @Test
    @DisplayName("GET /v1/report/{id} - Should return 200 with report data when report exists")
    void findById_ShouldReturnReportGetResponse_WhenReportExists() throws Exception {

        var savedReport = ReportUtils.savedReport();
        var response = ReportUtils.asBaseResponse(savedReport);

        when(service.findById(EXISTING_ID)).thenReturn(response);

        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/get/report-by-id-200.json");

        mockMvc.perform(get(PATH_ID, response.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse));

        verify(service).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/report/{id} - Should return 404 when report is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenReportDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/get/report-not-found-404.json");

        var expectedErrorMessage = REPORT_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/report - Should return page with reports")
    void findAll_ShouldReturnReportPageResponse_WhenReportExist() throws Exception {
        var reportList = ReportUtils.baseResponseList();
        var reportPage = PageUtils.toPage(reportList);
        var pageResponse = PageUtils.pageResponse(reportPage);

        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/get/all-paged-reports-200.json");

        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 201 Created when report is saved successfully")
    void save_ShouldReturn201Created_WhenRequestIsValid() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/post/response-created-report-200.json");

        var savedReport = ReportUtils.savedReport();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        when(service.save(any(ReportBaseRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 400 when report data is inconsistent")
    void save_ShouldReturn400_WhenReportsDataIsInvalid() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/post/operation-mismatch-400.json");

        var expectedErrorMessage = OPERATION_PATIENT_MISMATCH.formatted(1L, 2L);

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new OperationMismatchException(expectedErrorMessage));

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).save(any(ReportBaseRequest.class));
    }


    @Test
    @DisplayName("POST /v1/report - Should return 404 when related resource does not exist")
    void save_ShouldReturn404_WhenPatientDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/post/resource-not-found-404.json");

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedJsonResponse));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 409 when report already exists")
    void save_ShouldReturn409_WhenReportAlreadyExists() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/post/report-already-exists-409.json");

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ReportAlreadyExistsException(EXISTING_ID));

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(REPORT_ALREADY_EXISTS.formatted(EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 422 when request is invalid")
    void save_ShouldReturn422_WhenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("report/controller/post/request-create-report-invalid-422.json");
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/post/validation-error-422.json");

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json(expectedJsonResponse));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 200 OK when report is updated successfully")
    void update_ShouldReturn200OK_WhenRequestIsValid() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/put/response-updated-report-200.json");

        var updatedReport = ReportUtils.updatedReport();
        var response = ReportUtils.asBaseResponse(updatedReport);

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID))).thenReturn(response);

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 400 when report data is inconsistent")
    void update_ShouldReturn400_WhenReportDataIsInvalid() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/put/operation-mismatch-400.json");

        var expectedErrorMessage = OPERATION_PATIENT_MISMATCH.formatted(1L, 2L);

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new OperationMismatchException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 404 when report is not found")
    void update_ShouldReturn404_WhenReportDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/put/report-not-found-404.json");

        var expectedErrorMessage = REPORT_NOT_FOUND;

        when(service.update(any(ReportBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).update(any(ReportBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 404 when related resource does not exist")
    void update_ShouldReturn404_WhenPatientDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/put/resource-not-found-404.json");

        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.update(any(ReportBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).update(any(ReportBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 409 when report already exists")
    void update_ShouldReturn409_WhenReportAlreadyExists() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/put/report-already-exists-409.json");

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new ReportAlreadyExistsException(EXISTING_ID));

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(REPORT_ALREADY_EXISTS.formatted(EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 422 when request is invalid")
    void update_ShouldReturn422_WhenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("report/controller/put/request-create-report-invalid-422.json");
        var expectedJsonResponse = FileUtils.readResourceFile("report/controller/put/validation-error-422.json");

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service, never()).update(any(), anyLong());
    }

    @Test
    @DisplayName("DELETE /v1/report/{id} - Should return 204 No Content when report is deleted successfully")
    void delete_ShouldReturn204NoContent_WhenReportExists() throws Exception {
        doNothing().when(service).delete(EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).delete(EXISTING_ID);
    }

    @Test
    @DisplayName("DELETE /v1/report/{id} - Should return 404 Not Found when report does not exist")
    void delete_ShouldReturn404NotFound_WhenReportDoesNotExist() throws Exception {
        var expectedErrorMessage = REPORT_NOT_FOUND;

        BDDMockito.doThrow(new ResourceNotFoundException(expectedErrorMessage))
                .when(service).delete(NON_EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).delete(NON_EXISTING_ID);
    }

}