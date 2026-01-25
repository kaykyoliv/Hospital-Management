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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static com.kayky.commons.TestConstants.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Report Controller")
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
    @DisplayName("GET /v1/report/{id} - Should return 200 when report exists")
    void getReport_shouldReturn200_whenExists() throws Exception {
        var savedReport = ReportUtils.savedReport();
        var response = ReportUtils.asBaseResponse(savedReport);

        when(service.findById(EXISTING_ID)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, response.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("report/controller/get/report-by-id-200.json")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/report/{id} - Should return 404 when report does not exist")
    void getReport_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = REPORT_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("report/controller/get/report-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/report - Should return paged result when reports exist")
    void getReports_shouldReturnPagedResults_whenReportsExist() throws Exception {
        var reportList = ReportUtils.baseResponseList();
        var reportPage = PageUtils.toPage(reportList);
        var pageResponse = PageUtils.pageResponse(reportPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/get/all-paged-reports-200.json")))
                .andExpect(jsonPath("$.content").isArray());

        verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 201 when request is valid")
    void createReport_shouldReturn201_whenRequestIsValid() throws Exception {
        var savedReport = ReportUtils.savedReport();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        when(service.save(any(ReportBaseRequest.class))).thenReturn(expectedResponse);

        performPostRequest(validCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/v1/report/" + EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/response-created-report-200.json")));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 400 when operation does not match patient")
    void createReport_shouldReturn400_whenOperationDoesNotMatchPatient() throws Exception {
        var expectedErrorMessage = OPERATION_PATIENT_MISMATCH.formatted(1L, 2L);

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new OperationMismatchException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/operation-mismatch-patient-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 400 when operation does not match doctor")
    void createReport_shouldReturn400_whenOperationDoesNotMatchDoctor() throws Exception {
        var expectedErrorMessage = OPERATION_DOCTOR_MISMATCH.formatted(1L, 2L);

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new OperationMismatchException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/operation-mismatch-doctor-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(ReportBaseRequest.class));
    }


    @Test
    @DisplayName("POST /v1/report - Should return 404 when patient does not exist")
    void createReport_shouldReturn404_WhenPatientDoesNotExist() throws Exception {
        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/patient-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 404 when doctor does not exist")
    void create_shouldReturn404_whenDoctorNotFound() throws Exception{
        var expectedErrorMessage = DOCTOR_NOT_FOUND;

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/doctor-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 404 when operation does not exist")
    void create_shouldReturn404_whenOperationNotFound() throws Exception{
        var expectedErrorMessage = OPERATION_NOT_FOUND;

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/operation-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(ReportBaseRequest.class));
    }


    @Test
    @DisplayName("POST /v1/report - Should return 409 when report already exists")
    void createReport_shouldReturn409_whenReportAlreadyExist() throws Exception {
        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ReportAlreadyExistsException(EXISTING_ID));

        performPostRequest(validCreateRequest)
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/report-already-exists-409.json")));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 422 when request is invalid")
    void createReport_shouldReturn422_WhenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("report/controller/post/request-create-report-invalid-422.json");

        performPostRequest(invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/post/validation-error-422.json")));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 200 when request is valid")
    void updateReport_shouldReturn200_whenRequestIsValid() throws Exception {
        var updatedReport = ReportUtils.updatedReport();
        var response = ReportUtils.asBaseResponse(updatedReport);

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID))).thenReturn(response);

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/response-updated-report-200.json")));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - should return 400 when operation patient does not match")
    void updateReport_shouldReturn400_whenOperationPatientDoesNotMatch() throws Exception {
        var expectedErrorMessage = OPERATION_PATIENT_MISMATCH.formatted(1L, 2L);

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new OperationMismatchException(expectedErrorMessage));

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/operation-mismatch-patient-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - should return 400 when operation doctor does not match")
    void updateReport_shouldReturn400_whenOperationDoctorDoesNotMatch() throws Exception {
        var expectedErrorMessage = OPERATION_DOCTOR_MISMATCH.formatted(1L, 2L);

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new OperationMismatchException(expectedErrorMessage));

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/operation-mismatch-doctor-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id}- Should return 404 when report does not exist")
    void updateReport_shouldReturn404_WhenReportDoesNotExist() throws Exception {
        var expectedErrorMessage = REPORT_NOT_FOUND;

        when(service.update(any(ReportBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(NON_EXISTING_ID, validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/report-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(ReportBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id}- Should return 404 when patient does not exist")
    void updateReport_shouldReturn404_whenPatientDoesNotExist() throws Exception {
        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(EXISTING_ID, validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/patient-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 404 when doctor does not exist")
    void updateReport_shouldReturn404_whenDoctorDoesNotExist() throws Exception{
        var expectedErrorMessage = DOCTOR_NOT_FOUND;

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(EXISTING_ID, validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/doctor-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 404 when operation does not exist")
    void updateReport_shouldReturn404_whenOperationDoesNotExist() throws Exception{
        var expectedErrorMessage = OPERATION_NOT_FOUND;

        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(EXISTING_ID, validCreateRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/operation-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 409 when report already exists")
    void updateReport_shouldReturn409_whenReportAlreadyExists() throws Exception {
        when(service.update(any(ReportBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new ReportAlreadyExistsException(EXISTING_ID));

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/report-already-exists-409.json")))
                .andExpect(jsonPath("$.error").value(REPORT_ALREADY_EXISTS.formatted(EXISTING_ID)));

        verify(service).update(any(ReportBaseRequest.class), eq(EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/report/{id} - Should return 422 when request is invalid")
    void updateReport_shouldReturn422_whenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("report/controller/put/request-create-report-invalid-422.json");

        performPutRequest(EXISTING_ID, invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/put/validation-error-422.json")));

        verify(service, never()).update(any(), anyLong());
    }

    @Test
    @DisplayName("DELETE /v1/report/{id} - Should return 204 No Content when report is deleted successfully")
    void delete_shouldReturn204_whenReportExists() throws Exception {
        doNothing().when(service).delete(EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).delete(EXISTING_ID);
    }

    @Test
    @DisplayName("DELETE /v1/report/{id} - Should return 404 Not Found when report does not exist")
    void delete_shouldReturn404_whenReportDoesNotExist() throws Exception {
        var expectedErrorMessage = REPORT_NOT_FOUND;

        BDDMockito.doThrow(new ResourceNotFoundException(expectedErrorMessage))
                .when(service).delete(NON_EXISTING_ID);

        mockMvc.perform(delete(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("report/controller/delete/report-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).delete(NON_EXISTING_ID);
    }
}