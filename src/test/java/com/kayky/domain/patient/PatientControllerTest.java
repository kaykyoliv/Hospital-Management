package com.kayky.domain.patient;

import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.PatientUtils;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.patient.request.PatientBaseRequest;
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
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Patient Controller")
@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {

    private static final String BASE_URI = "/v1/patient";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService service;

    private String validCreateRequest;
    private String validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = FileUtils.readResourceFile("patient/controller/post/request/request-create-patient-201.json");
        validUpdateRequest = FileUtils.readResourceFile("patient/controller/put/request/request-update-patient-200.json");
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
    @DisplayName("GET /v1/patient/{id} - Should return 200 when patient exists")
    void getPatient_shouldReturn200_whenExists() throws Exception {
        var patientId = EXISTING_ID;
        var savedPatient = PatientUtils.savedPatient(patientId);
        var response = PatientUtils.asBaseResponse(savedPatient);

        when(service.findById(EXISTING_ID)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, patientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("patient/controller/get/patient-by-id-200.json")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/patient/{id} - Should return 404 when patient does not exist")
    void getPatient_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("patient/controller/get/patient-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/patient - Should return paged result when patients exist")
    void getPatients_shouldReturnPagedResults_whenPatientsExist() throws Exception {
        var patientList = PatientUtils.asBaseResponseList();
        var patientPage = PageUtils.toPage(patientList);
        var pageResponse = PageUtils.pageResponse(patientPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/get/all-paged-patients-200.json")))
                .andExpect(jsonPath("$.content").isArray());


        verify(service).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/patient - Should return 201 when request is valid")
    void createPatient_shouldReturn201_whenRequestIsValid() throws Exception {
        var savedPatient = PatientUtils.savedPatient(EXISTING_ID);
        var expectedResponse = PatientUtils.asBaseResponse(savedPatient);

        when(service.save(any(PatientBaseRequest.class))).thenReturn(expectedResponse);

        performPostRequest(validCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/v1/patient/" + EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/post/response/response-created-patient-201.json")));


        verify(service).save(any(PatientBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/patient - Should return 400 when email already exists")
    void createPatient_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        var request = PatientUtils.asBaseRequest();

        var expectedErrorMessage = EMAIL_ALREADY_EXISTS.formatted(request.getEmail());

        when(service.save(any(PatientBaseRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/post/response/response-email-already-exists-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(PatientBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/patient - Should return 422 when request is invalid")
    void createPatient_shouldReturn422_whenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("patient/controller/post/request/request-create-patient-invalid-422.json");

        performPostRequest(invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/post/response/response-validation-error-422.json")));

        verify(service, never()).save(any());
    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 200 when request is valid")
    void updatePatient_shouldReturn200_whenRequestIsValid() throws Exception {
        var updatedPatient = PatientUtils.updatedPatient();
        var patientId = updatedPatient.getId();
        var expectedResponse = PatientUtils.asBaseResponse(updatedPatient);

        when(service.update(any(PatientBaseRequest.class), eq(patientId))).thenReturn(expectedResponse);

        performPutRequest(patientId, validUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/put/response/response-updated-patient-200.json")));

        verify(service).update(any(PatientBaseRequest.class), eq(patientId));
    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 400 when email already exists")
    void updatePatient_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        var patientId = EXISTING_ID;
        var savedPatient = PatientUtils.savedPatient(patientId);

        var expectedErrorMessage = EMAIL_ALREADY_EXISTS.formatted(savedPatient.getEmail());

        when(service.update(any(PatientBaseRequest.class), eq(patientId)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        performPutRequest(patientId, validUpdateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/put/response/response-email-already-exists-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(PatientBaseRequest.class), eq(patientId));
    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 404 when patient does not exist")
    void updatePatient_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = PATIENT_NOT_FOUND;

        when(service.update(any(PatientBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(NON_EXISTING_ID, validUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(loadExpectedJson("patient/controller/put/response/response-patient-not-found-404.json")));

        verify(service).update(any(PatientBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/patient/{id} - Should return 422 when request is invalid")
    void updateDoctor_shouldReturn422_whenRequestIsInvalid() throws Exception {
        var invalidRequest = FileUtils.readResourceFile("patient/controller/put/request/request-update-patient-invalid-422.json");

        performPutRequest(EXISTING_ID, invalidRequest)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("patient/controller/put/response/response-validation-error-422.json")));

        verify(service, never()).update(any(), anyLong());
    }
}