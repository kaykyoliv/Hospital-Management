package com.kayky.domain.doctor;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.FileUtils;
import com.kayky.commons.PageUtils;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


}