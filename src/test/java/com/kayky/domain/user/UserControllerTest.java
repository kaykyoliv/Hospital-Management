package com.kayky.domain.user;

import com.kayky.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static com.kayky.commons.TestConstants.*;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private static final String BASE_URI = "/v1/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController controller;

    @MockitoBean
    private UserService service;

    @Test
    @DisplayName("PATCH /users/{id}/activate - Should return 204 No Content when activation succeeds")
    void activateUser_ShouldReturnNoContent_WhenUserIsInactive() throws Exception {
        doNothing().when(service).activateUser(EXISTING_ID);

        mockMvc.perform(patch(BASE_URI + "/{id}/activate", EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service).activateUser(EXISTING_ID);
    }

    @Test
    @DisplayName("PATCH /users/{id}/activate - Should return 204 No Content when activation succeeds")
    void deactivateUser_ShouldReturnNoContent_WhenUserIsActive() throws Exception {
        doNothing().when(service).deactivateUser(EXISTING_ID);

        mockMvc.perform(patch(BASE_URI + "/{id}/deactivate", EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service).deactivateUser(EXISTING_ID);
    }

    @Test
    @DisplayName("PATCH /users/{id}/activate - Should return 404 Not Found when user does not exist")
    void activateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {

        doThrow(new ResourceNotFoundException(USER_NOT_FOUND))
                .when(service)
                .activateUser(NON_EXISTING_ID);

        mockMvc.perform(patch(BASE_URI + "/{id}/activate", NON_EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PATCH /users/{id}/deactivate - Should return 404 Not Found when user does not exist")
    void deactivateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {

        doThrow(new ResourceNotFoundException(USER_NOT_FOUND))
                .when(service)
                .deactivateUser(NON_EXISTING_ID);

        mockMvc.perform(patch(BASE_URI + "/{id}/deactivate", NON_EXISTING_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /users/{id}/activate - Should return 400 Bad Request when user is already active")
    void activateUser_ShouldReturnBadRequest_WhenUserIsAlreadyActive() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(service)
                .activateUser(EXISTING_ID);

        mockMvc.perform(patch(BASE_URI + "/{id}/activate", EXISTING_ID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/{id}/deactivate - Should return 400 Bad Request when user is already inactive")
    void deactivateUser_ShouldReturnBadRequest_WhenUserIsAlreadyInactive() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(service)
                .deactivateUser(EXISTING_ID);

        mockMvc.perform(patch(BASE_URI + "/{id}/deactivate", EXISTING_ID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


}