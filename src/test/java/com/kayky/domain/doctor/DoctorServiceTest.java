package com.kayky.domain.doctor;

import com.kayky.commons.DoctorUtils;
import com.kayky.commons.PageUtils;
import com.kayky.domain.cashier.Cashier;
import com.kayky.domain.user.UserValidator;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Doctor Service - Unit Tests")
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    private DoctorService service;

    @Mock
    private DoctorRepository repository;

    private final DoctorMapper mapper = Mappers.getMapper(DoctorMapper.class);

    @Mock
    private UserValidator userValidator;

    @BeforeEach
    void setUp (){
        service = new DoctorService(repository, mapper, userValidator);
    }


    @Test
    @DisplayName("findById: Should return DoctorBaseResponse when doctor exists")
    void findById_shouldReturnBaseResponse_whenDoctorExists() {
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedDoctor));

        var doctorFound = service.findById(EXISTING_ID);

        Assertions.assertThat(doctorFound)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("findById - Should throw not-found exception when doctor does not exist")
    void findById_shouldThrowNotFound_whenDoctorDoesNotExist() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(DOCTOR_NOT_FOUND );

        verify(repository).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("findAll - Should return paged response when doctors exist")
    void findAll_shouldReturnPagedResponse_whenDoctorsExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var doctorList = DoctorUtils.doctorList();
        var pagedDoctor = PageUtils.toPage(doctorList);

        when(repository.findAll(pageRequest)).thenReturn(pagedDoctor);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedDoctor.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedDoctor.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedDoctor.getNumber());

        var expectedResponse = DoctorUtils.asBaseResponseList();

        assertThat(result.getContent())
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findAll(pageRequest);
    }


    @Test
    @DisplayName("save - Should return base response when request is valid")
    void save_shouldReturnBaseResponse_whenCreatingValidDoctor() {
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);

        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);
        var request = DoctorUtils.asBaseRequest();

        doNothing().when(userValidator).assertEmailDoesNotExist(request.getEmail());

        when(repository.save(any(Doctor.class))).thenReturn(savedDoctor);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(userValidator).assertEmailDoesNotExist(request.getEmail());
        verify(repository).save(any(Doctor.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("save - Should throw email-already-exists exception when email is in use")
    void save_shouldThrowEmailAlreadyExists_whenCreatingWithDuplicateEmail() {
        var request = DoctorUtils.asBaseRequest();
        var email = request.getEmail();

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(email);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXISTS.formatted(email));


        verify(userValidator).assertEmailDoesNotExist(email);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("update - Should return base response when request is valid")
    void update_shouldReturnBaseResponse_whenUpdatingValidDoctor() {
        var doctorId = EXISTING_ID;
        var savedDoctor = DoctorUtils.savedDoctor(doctorId);

        var request = DoctorUtils.asBaseRequest();
        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);

        doNothing().when(userValidator).assertEmailDoesNotExist(request.getEmail(), doctorId);
        when(repository.findById(doctorId)).thenReturn(Optional.of(savedDoctor));
        when(repository.save(any(Doctor.class))).thenReturn(savedDoctor);

        var result = service.update(request, doctorId);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(userValidator).assertEmailDoesNotExist(request.getEmail(), doctorId);
        verify(repository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("update - Should throw not-found exception when doctor does not exist")
    void update_shouldThrowNotFound_whenUpdatingNonExistingDoctor() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(DoctorUtils.asBaseRequest(), NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(DOCTOR_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("update - Should throw email-already-exists exception when email is in use")
    void update_shouldThrowEmailAlreadyExists_whenUpdatingWithDuplicateEmail() {
        var doctorId = EXISTING_ID;
        var savedDoctor = DoctorUtils.savedDoctor(doctorId);
        var request = DoctorUtils.asBaseRequest();
        var email = request.getEmail();

        when(repository.findById(doctorId)).thenReturn(Optional.of(savedDoctor));

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(request.getEmail(), doctorId);

        assertThatThrownBy(() -> service.update(request, doctorId))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXISTS.formatted(email));

        verify(userValidator).assertEmailDoesNotExist(request.getEmail(), doctorId);
    }
}