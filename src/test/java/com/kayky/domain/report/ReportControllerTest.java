package com.kayky.domain.report;

import com.kayky.commons.FileUtils;
import com.kayky.commons.ReportUtils;
import com.kayky.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void findById_ShouldReturnReportGetResponse_WhenOperationExists() throws Exception{

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
}