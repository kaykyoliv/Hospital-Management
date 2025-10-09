package com.kayky.domain.doctor;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
import com.kayky.domain.doctor.request.DoctorBaseRequest;
import com.kayky.exception.EmailAlreadyExistsException;
import com.kayky.exception.ResourceNotFoundException;
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

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    private static final String BASE_URI = "/v1/doctor";
    private static final String PATH_ID = BASE_URI + "/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService service;

    @Test
    @DisplayName("GET /v1/doctor/{id} - Should return 200 with doctor data when doctor exists")
    void findById_ShouldReturnDoctorGetResponse_WhenDoctorExists() throws Exception {
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var response = DoctorUtils.asBaseResponse(savedDoctor);

        BDDMockito.when(service.findById(EXISTING_ID)).thenReturn(response);

        var expectedResponse = FileUtils.readResourceFile("doctor/get/doctor-by-id-200.json");

        mockMvc.perform(get(PATH_ID, response.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        BDDMockito.verify(service).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("GET /v1/doctor/{id} - Should return 404 when doctor is not found")
    void findById_ShouldThrowResourceNotFoundException_WhenDoctorDoesNotExist() throws Exception {
        var expectedJsonResponse = FileUtils.readResourceFile("doctor/get/doctor-not-found-404.json");

        var expectedErrorMessage = DOCTOR_NOT_FOUND;

        BDDMockito.when(service.findById(NON_EXISTING_ID))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(get(PATH_ID, NON_EXISTING_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("GET /v1/doctor - Should return page with doctors")
    void findAll_ShouldReturnDoctorPageResponse_WhenDoctorsExist() throws Exception {
        var doctorList = DoctorUtils.asBaseResponseList();
        var doctorPage = PageUtils.toPage(doctorList);
        var pageResponse = PageUtils.pageResponse(doctorPage);

        var expectedJsonResponse =  FileUtils.readResourceFile("doctor/get/all-paged-doctors-200.json");

        BDDMockito.when(service.findAll(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get(BASE_URI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).findAll(any(Pageable.class));
    }


    @Test
    @DisplayName("POST /v1/doctor - Should return 201 Created when doctor is saved successfully")
    void save_ShouldReturnPostResponse_WhenEmailIsUnique() throws Exception {

        var request =  FileUtils.readResourceFile("doctor/post/request-create-doctor-201.json");
        var expectedJsonResponse =  FileUtils.readResourceFile("doctor/post/response-created-doctor-200.json");

        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);

        BDDMockito.when(service.save(any(DoctorBaseRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URI)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse))
                .andExpect(header().string("Location", "http://localhost/v1/doctor/1"));

        BDDMockito.verify(service).save(any(DoctorBaseRequest.class));
    }

    @Test
    @DisplayName("POST /v1/doctor - Should return 400 when email already exists")
    void save_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() throws Exception {

        var request = FileUtils.readResourceFile("doctor/post/request-create-doctor-201.json");
        var expectedJsonResponse = FileUtils.readResourceFile("doctor/post/response-email-already-exists-400.json");

        var postRequest= DoctorUtils.asBaseRequest();

        var expectedErrorMessage = EMAIL_ALREADY_EXIST.formatted(postRequest.getEmail());

        BDDMockito.when(service.save(any(DoctorBaseRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        mockMvc.perform(post(BASE_URI)
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));


        BDDMockito.verify(service).save(any(DoctorBaseRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/doctor/{id} - Should return 200 and updated doctor when request is valid")
    void update_ShouldReturnPutResponse_WhenUpdateIsValid() throws Exception {

        var request =  FileUtils.readResourceFile("doctor/put/request-update-doctor.json");
        var expectedJsonResponse =  FileUtils.readResourceFile("doctor/put/response-updated-doctor.json");

        var updatedDoctor = DoctorUtils.updatedDoctor();
        var doctorId = updatedDoctor.getId();
        var expectedResponse = DoctorUtils.asBaseResponse(updatedDoctor);

        BDDMockito.when(service.update(any(DoctorBaseRequest.class), eq(doctorId))).thenReturn(expectedResponse);

        mockMvc.perform(put(PATH_ID, doctorId)
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).update(any(DoctorBaseRequest.class), eq(doctorId));
    }

    @Test
    @DisplayName("PUT /v1/doctor/{id} - Should return 404 when doctor does not exist")
    void update_ShouldThrowResourceNotFoundException_WhenDoctorDoesNotExist() throws Exception {
        var request = FileUtils.readResourceFile("doctor/put/request-update-doctor.json");
        var expectedJsonResponse = FileUtils.readResourceFile("doctor/put/response-doctor-not-found-404.json");

        var expectedErrorMessage = DOCTOR_NOT_FOUND;

        BDDMockito.when(service.update(any(DoctorBaseRequest.class), eq(NON_EXISTING_ID)))
                .thenThrow(new ResourceNotFoundException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, NON_EXISTING_ID)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).update(any(DoctorBaseRequest.class), eq(NON_EXISTING_ID));
    }

    @Test 
    @DisplayName("PUT /v1/doctor/{id}  - Should return 400 when email already exists")
    void update_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() throws Exception {

        var request = FileUtils.readResourceFile("doctor/put/request-update-doctor.json");
        var expectedJsonResponse = FileUtils.readResourceFile("doctor/put/response-email-already-exists-400.json");

        var putRequest= DoctorUtils.asBaseRequest();
        var expectedErrorMessage = EMAIL_ALREADY_EXIST.formatted(putRequest.getEmail());

        BDDMockito.when(service.update(any(DoctorBaseRequest.class), eq(EXISTING_ID)))
                .thenThrow(new EmailAlreadyExistsException(expectedErrorMessage));

        mockMvc.perform(put(PATH_ID, EXISTING_ID)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage))
                .andExpect(content().json(expectedJsonResponse));

        BDDMockito.verify(service).update(any(DoctorBaseRequest.class), eq(EXISTING_ID));
    }


}