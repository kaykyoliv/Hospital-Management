package com.kayky.domain.doctor;

import com.kayky.commons.DoctorUtils;
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
    @DisplayName("findById: Should return DoctorBaseResponse when the doctor exists")
    void findBydId_ShouldReturnDoctorBaseResponse_WhenDoctorExists(){
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);

        BDDMockito.when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedDoctor));

        var doctorFound = service.findById(EXISTING_ID);

        Assertions.assertThat(doctorFound)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("findById: Should throw ResourceNotFoundException when the doctor does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenDoctorDoesNotExist() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(DOCTOR_NOT_FOUND );

        verify(repository).findById(NON_EXISTING_ID);
    }


    @Test
    @DisplayName("findAll: Should return PageResponse when doctors exist")
    void findAll_ShouldReturnPageResponse_WhenDoctorsExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var doctorList = DoctorUtils.doctorList();
        var pagedDoctor = new PageImpl<>(doctorList, pageRequest, doctorList.size());

        when(repository.findAll(pageRequest)).thenReturn(pagedDoctor);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedDoctor.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedDoctor.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedDoctor.getNumber());

        var expectedResponse = doctorList.stream()
                .map(mapper::toDoctorBaseResponse)
                .toList();

        assertThat(result.getContent())
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findAll(pageRequest);
    }

    @Test
    @DisplayName("save: Should return DoctorBaseResponse when email is unique")
    void save_ShouldReturnDoctorBaseResponse_WhenEmailIsUnique() {
        var request = DoctorUtils.asBaseRequest();


        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var expectedResponse = DoctorUtils.asBaseResponse(savedDoctor);

        when(repository.save(any(Doctor.class))).thenReturn(savedDoctor);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("save: Should throw EmailAlreadyExistsException when email is already in use")
    void save_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() {
        var request = DoctorUtils.asBaseRequest();

        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var email = savedDoctor.getEmail();

        request.setEmail(email);

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXIST.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(email);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXIST.formatted(email));

        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("update: Should return DoctorBaseResponse when update is valid")
    void update_ShouldReturnDoctorBaseResponse_WhenUpdateIsValid() {
        var putRequest = DoctorUtils.asBaseRequest();
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var updatedDoctor = DoctorUtils.updatedDoctor();

        var expectedResponse = DoctorUtils.asBaseResponse(updatedDoctor);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedDoctor));
        when(repository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        var result = service.update(putRequest, EXISTING_ID);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
        verify(repository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("update: Should throw ResourceNotFoundException when doctor does not exist")
    void update_ShouldThrowResourceNotFoundException_WhenDoctorDoesNotExist() {
        var request = DoctorUtils.asBaseRequest();

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request, NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(DOCTOR_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("update: Should throw EmailAlreadyExistsException when email is used by another doctor")
    void update_ShouldThrowEmailAlreadyExistsException_WhenEmailUsedByAnotherDoctor() {
        var savedDoctor = DoctorUtils.savedDoctor(EXISTING_ID);
        var email = savedDoctor.getEmail();
        var request = DoctorUtils.asBaseRequest();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedDoctor));

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXIST.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(request.getEmail(), EXISTING_ID);

        assertThatThrownBy(() -> service.update(request, EXISTING_ID))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXIST.formatted(email));

        verify(userValidator).assertEmailDoesNotExist(request.getEmail(), EXISTING_ID);
    }
}