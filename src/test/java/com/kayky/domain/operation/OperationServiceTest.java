package com.kayky.domain.operation;

import com.kayky.commons.OperationUtils;
import com.kayky.domain.doctor.DoctorRepository;
import com.kayky.domain.patient.PatientRepository;
import com.kayky.domain.user.UserValidator;
import com.kayky.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    private OperationService service;

    @Mock
    private OperationRepository repository;

    @Mock
    private  PatientRepository patientRepository;

    @Mock
    private  DoctorRepository doctorRepository;

    @Mock
    private  UserValidator userValidator;

    private final OperationMapper mapper = Mappers.getMapper(OperationMapper.class);

    @BeforeEach
    void setUp(){
        service = new OperationService(repository, patientRepository, doctorRepository, userValidator, mapper);
    }

    @Test
    @DisplayName("findById: Should return OperationBaseResponse when the operation exists")
    void findById_ShouldReturnOperationBaseResponse_WhenOperationExists(){
        var savedOperation = OperationUtils.savedOperation();

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedOperation));

        var response = service.findById(EXISTING_ID);

        var expectedResponse = OperationUtils.asBaseResponse(savedOperation);

        Assertions.assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("findById: Should throw ResourceNotFoundException when the Operation does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenOperationDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(OPERATION_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll: Should return PageResponse when doctors exist")
    void findAll_ShouldReturnPageResponse_WhenOperationExists() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var pagedOperation = OperationUtils.operationProjectionPage();

        when(repository.findAllProjected(pageRequest)).thenReturn(pagedOperation);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedOperation.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedOperation.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedOperation.getNumber());

        var expectedResponse = OperationUtils.operationDetailsResponseList();

        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

}