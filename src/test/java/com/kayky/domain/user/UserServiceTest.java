package com.kayky.domain.user;

import com.kayky.commons.PatientUtils;
import com.kayky.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Test
    @DisplayName("activateUser - Should activate and save when user is deactivated")
    void activateUser_ShouldActivate_WhenUserIsDeactivate() {
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        patient.setActive(false);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(patient));

        service.activateUser(patient.getId());

        assertThat(patient.getActive()).isTrue();

        verify(repository).findById(EXISTING_ID);
        verify(repository).save(patient);
    }

    @Test
    @DisplayName("deactivateUser - Should deactivate and save when user is activated")
    void deactivateUser_ShouldDeactivate_WhenUserIsActivate() {
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        patient.setActive(true);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(patient));

        service.deactivateUser(patient.getId());

        assertThat(patient.getActive()).isFalse();

        verify(repository).findById(EXISTING_ID);
        verify(repository).save(patient);
    }

    @Test
    @DisplayName("activateUser - Should throw ResourceNotFoundException when user does not exist")
    void activateUser_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activateUser(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");


        verify(repository).findById(NON_EXISTING_ID);

    }


    @Test
    @DisplayName("deactivateUser - Should throw ResourceNotFoundException when user does not exist")
    void deactivateUser_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivateUser(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");


        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("activateUser - Should throw ResponseStatusException with BAD_REQUEST when user is already active")
    void activateUser_ShouldThrowResponseStatusException_WhenUserIsAlreadyActive() {
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        patient.setActive(true);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> service.activateUser(EXISTING_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("User already active");
                });

        verify(repository).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("deactivateUser - Should throw ResponseStatusException with BAD_REQUEST when user is already inactive")
    void deactivateUser_ShouldThrowResponseStatusException_WhenUserIsAlreadyInactive() {
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        patient.setActive(false);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> service.deactivateUser(EXISTING_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("User already inactive");
                });

        verify(repository).findById(EXISTING_ID);
    }

}