package com.kayky.domain.patient;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.PatientUtils;
import com.kayky.domain.patient.request.PatientBaseRequest;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {

    private static final String BASE_URI = "/v1/patient";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService service;

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 200 with patient data when patient exists")
    void findById_ShouldReturnPatientGetResponse_WhenPatientExists() throws Exception {
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var response = PatientUtils.asBaseResponse(savedPatient);

        BDDMockito.when(service.findById(EXISTING_ID)).thenReturn(response);

        var expectedJsonResponse = FileUtils.readResourceFile("patient/get/patient-by-id-200.json");

        mockMvc.perform(get(PATH_ID, response.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 404 when patient is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenPatientDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("patient/get/patient-not-found-404.json");

        var expectedErrorMessage = PATIENT_NOT_FOUND;

        BDDMockito.when(service.findById(NON_EXISTING_ID))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/patient - Should return page with patients")
    void findAll_ShouldReturnPatientPageResponse_WhenPatientsExist() throws Exception {
        var patientList = PatientUtils.patientBaseResponseList();
        var patientPage = PageUtils.toPage(patientList);
        var pageResponse = PageUtils.pageResponse(patientPage);

        var expectedJsonResponse =  FileUtils.readResourceFile("patient/get/all-paged-patients-200.json");

        BDDMockito.when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/patient - Should return 201 Created when patient is saved successfully")
    void save_ShouldReturnPostResponse_WhenEmailIsUnique() throws Exception {
        var request = FileUtils.readResourceFile("patient/post/request-create-patient-201.json");
        var expectedJsonResponse =  FileUtils.readResourceFile("patient/post/response-created-patient-200.json");

        var patient = PatientUtils.savedPatient(EXISTING_ID);
        var expectedResponse = PatientUtils.asBaseResponse(patient);

        BDDMockito.when(service.save(any(PatientBaseRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URI)
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse))
                .andExpect(header().string("Location", "http://localhost/v1/patient/1"));

        BDDMockito.verify(service).save(any(PatientBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/patient - Should return 400 when email already exists")
    void save_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() throws Exception {
        var request = FileUtils.readResourceFile("patient/post/request-create-patient-201.json");
        var expectedJsonResponse =  FileUtils.readResourceFile("patient/post/response-email-already-exists-400.json");

        var postRequest= PatientUtils.asBaseRequest();
        var expectedErrorMessage = EMAIL_ALREADY_EXIST.formatted(postRequest.getEmail());


        BDDMockito.when(service.save(any(PatientBaseRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        mockMvc.perform(post(BASE_URI)
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).save(any(PatientBaseRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 200 and updated patient when request is valid")
    void update_ShouldReturnPutResponse_WhenUpdateIsValid() throws Exception {
        var request = FileUtils.readResourceFile("patient/put/request-update-patient.json");
        var expectedJsonResponse =  FileUtils.readResourceFile("patient/put/response-updated-patient-200.json");

        var updatedPatient = PatientUtils.updatedPatient();
        var expectedResponse = PatientUtils.asBaseResponse(updatedPatient);

        BDDMockito.when(service.update(any(PatientBaseRequest.class), eq(EXISTING_ID))).thenReturn(expectedResponse);

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).update(any(PatientBaseRequest.class), eq(EXISTING_ID));

    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 404 when patient does not exist")
    void update_ShouldThrowResourceNotFoundException_WhenPatientDoesNotExist() throws Exception {
        var request = FileUtils.readResourceFile("patient/put/request-update-patient.json");
        var expectedJsonResponse = FileUtils.readResourceFile("patient/put/response-patient-not-found-404.json");

        var expectedErrorMessage = PATIENT_NOT_FOUND;

        BDDMockito.when(service.update(any(PatientBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, NON_EXISTING_ID)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedJsonResponse))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        BDDMockito.verify(service).update(any(PatientBaseRequest.class), eq(NON_EXISTING_ID));

    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 400 when email is already used by another patient")
    void update_ShouldThrowEmailAlreadyExistsException_WhenEmailUsedByAnotherPatient() throws Exception {
        var request = FileUtils.readResourceFile("patient/put/request-update-patient.json");
        var expectedJsonResponse = FileUtils.readResourceFile("patient/put/response-email-already-exists-400.json");

        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var expectedErrorMessage = EMAIL_ALREADY_EXIST.formatted(savedPatient.getEmail());

        BDDMockito.when(service.update(any(PatientBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResponse))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        BDDMockito.verify(service).update(any(PatientBaseRequest.class), eq(EXISTING_ID));
    }
}