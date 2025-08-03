package com.kayky.domain.patient;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PatientUtils;
import com.kayky.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {

    private static final String URI = "/v1/patient";
    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;
    private static final String PATIENT_NOT_FOUND = "Patient not found";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService service;

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 200 with patient data when patient exists")
    void findById_ShouldReturnPatientGetResponse_WhenPatientExists() throws Exception {
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var response = PatientUtils.asGetResponse(savedPatient);

        BDDMockito.when(service.findById(EXISTING_ID)).thenReturn(response);

        var expectedJsonResponse = FileUtils.readResourceFile("patient/get/patient-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", response.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 404 when patient is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenPatientDoesNotExist() throws Exception {

        BDDMockito.when(service.findById(NON_EXISTING_ID))
                .thenThrow(new ResourceNotFoundException(PATIENT_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", NON_EXISTING_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.error").value(PATIENT_NOT_FOUND));

    }
}