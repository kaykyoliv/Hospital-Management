package com.kayky.domain.doctor;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.domain.doctor.request.DoctorBaseRequest;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Doctor Controller")
@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    private static final String BASE_URI = "/v1/doctor";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService service;

    private String validCreateRequest;
    private String validUpdateRequest;

    @BeforeEach
    void setUp() {
        validCreateRequest = FileUtils.readResourceFile("doctor/controller/post/request/request-create-doctor-201.json");
        validUpdateRequest = FileUtils.readResourceFile("doctor/controller/put/request/request-update-doctor-200.json");
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
    @DisplayName("GET /v1/doctor/{id} - Should return 200 when doctor exists")
    void getDoctor_shouldReturn200_whenExists() throws Exception {
        var doctorId = EXISTING_ID;
        var savedDoctor = DoctorUtils.savedDoctor(doctorId);
        var response = DoctorUtils.asBaseResponse(savedDoctor);

        when(service.findById(doctorId)).thenReturn(response);

        mockMvc.perform(get(PATH_ID, doctorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(loadExpectedJson("doctor/controller/get/doctor-by-id-200.json")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/doctor/{id} - Should return 404 when doctor does not exist")
    void getDoctor_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = DOCTOR_NOT_FOUND;

        when(service.findById(NON_EXISTING_ID)).thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(loadExpectedJson("doctor/controller/get/doctor-not-found-404.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/doctor - Should return paged result when doctors exist")
    void getDoctors_shouldReturnPagedResults_whenDoctorsExist() throws Exception {
        var doctorList = DoctorUtils.asBaseResponseList();
        var doctorPage = PageUtils.toPage(doctorList);
        var pageResponse = PageUtils.pageResponse(doctorPage);

        when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("doctor/controller/get/all-paged-doctors-200.json")))
                .andExpect(jsonPath("$.content").isArray());

        verify(service).findAll(any(Pageable.class));
    }


    @Test
    @DisplayName("POST /v1/doctor - Should return 201 when request is valid")
    void createDoctor_shouldReturn201_whenRequestIsValid() throws Exception {
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);

        when(service.save(any(DoctorBaseRequest.class))).thenReturn(expectedResponse);

        performPostRequest(validCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/v1/doctor/" + EXISTING_ID)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("doctor/controller/post/response/response-created-doctor-201.json")));

        verify(service).save(any(DoctorBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/doctor - Should return 400 when email already exists")
    void createDoctor_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        var request = DoctorUtils.asBaseRequest();

        var expectedErrorMessage = EMAIL_ALREADY_EXISTS.formatted(request.getEmail());

        when(service.save(any(DoctorBaseRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        performPostRequest(validCreateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("doctor/controller/post/response/response-email-already-exists-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).save(any(DoctorBaseRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/doctor/{id} - Should return 200 when request is valid")
    void updateDoctor_shouldReturn200_whenRequestIsValid() throws Exception {
        var updatedDoctor = DoctorUtils.updatedDoctor();
        var doctorId = updatedDoctor.getId();
        var expectedResponse = DoctorUtils.asBaseResponse(updatedDoctor);

        when(service.update(any(DoctorBaseRequest.class), eq(doctorId))).thenReturn(expectedResponse);

        performPutRequest(EXISTING_ID, validUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("doctor/controller/put/response/response-updated-doctor-200.json")));

        verify(service).update(any(DoctorBaseRequest.class), eq(doctorId));
    }

    @Test
    @DisplayName("PUT /v1/doctor/{id} - Should return 404 when doctor does not exist")
    void updateDoctor_shouldReturn404_whenDoesNotExist() throws Exception {
        var expectedErrorMessage = DOCTOR_NOT_FOUND;

        when(service.update(any(DoctorBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        performPutRequest(NON_EXISTING_ID, validUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(loadExpectedJson("doctor/controller/put/response/response-doctor-not-found-404.json")));

        verify(service).update(any(DoctorBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test
    @DisplayName("PUT /v1/doctor/{id} - Should return 400 when email already exists")
    void updateDoctor_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        var doctorId = EXISTING_ID;
        var savedDoctor = DoctorUtils.savedDoctor(doctorId);

        var expectedErrorMessage = EMAIL_ALREADY_EXISTS.formatted(savedDoctor.getEmail());

        when(service.update(any(DoctorBaseRequest.class), eq(doctorId)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        performPutRequest(doctorId, validUpdateRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(loadExpectedJson("doctor/controller/put/response/response-email-already-exists-400.json")))
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));

        verify(service).update(any(DoctorBaseRequest.class), eq(doctorId));
    }
}