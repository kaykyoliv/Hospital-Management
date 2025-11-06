package com.kayky.domain.report;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.ReportUtils;
import com.kayky.core.exception.ReportAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.report.request.ReportBaseRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportController.class)
class ReportControllerTest {

    private static final String BASE_URI = "/v1/report";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService service;

    @Test
    @DisplayName("GET /v1/report/{id} - Should return 200 with report data when report exists")
    void findById_ShouldReturnReportGetResponse_WhenOperationExists() throws Exception {

        var savedReport = ReportUtils.savedReport();
        var response = ReportUtils.asBaseResponse(savedReport);

        when(service.findById(EXISTING_ID)).thenReturn(response);

        var expectedJsonResponse = FileUtils.readResourceFile("report/get/report-by-id-200.json");

        mockMvc.perform(get(PATH_ID, response.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/report/{id} - Should return 404 when report is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenReportDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("report/get/report-not-found-404.json");

        var expectedErrorMessage = REPORT_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/operation - Should return page with reports")
    void findAll_ShouldReturnOperationPageResponse_WhenReportExist() throws Exception {
        var reportList = ReportUtils.baseResponseList();
        var reportPage = PageUtils.toPage(reportList);
        var pageResponse = PageUtils.pageResponse(reportPage);

        var expectedJsonResponse = FileUtils.readResourceFile("report/get/all-paged-reports-200.json");

        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 201 Created when report is saved successfully")
    void save_ShouldReturn201Created_WhenRequestIsValid() throws Exception {
        var request = FileUtils.readResourceFile("report/post/request-create-report-201.json");
        var expectedJsonResponse = FileUtils.readResourceFile("report/post/response-created-report-200.json");

        var savedReport = ReportUtils.savedReport();
        var expectedResponse = ReportUtils.asBaseResponse(savedReport);

        when(service.save(any(ReportBaseRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).save(any(ReportBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/report - Should return 409 when report already exists")
    void save_ShouldReturn409_WhenReportAlreadyExists() throws Exception {
        var request = FileUtils.readResourceFile("report/post/request-create-report-201.json");
        var expectedJsonResponse = FileUtils.readResourceFile("report/post/report-already-exists-409.json");

        when(service.save(any(ReportBaseRequest.class)))
                .thenThrow(new ReportAlreadyExistsException(EXISTING_ID));

        mockMvc.perform(post(BASE_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        verify(service).save(any(ReportBaseRequest.class));
    }
}