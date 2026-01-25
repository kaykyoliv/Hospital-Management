package com.kayky.domain.payment;


import com.kayky.commons.CashierUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.PatientUtils;
import com.kayky.commons.PaymentUtils;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.cashier.CashierRepository;
import com.kayky.domain.patient.PatientRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;

@DisplayName("Payment Service - Unit Tests")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private PaymentService service;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private CashierRepository cashierRepository;

    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @BeforeEach
    void setUp() {
        service = new PaymentService(paymentRepository, patientRepository, cashierRepository, mapper);
    }

    @Test
    @DisplayName("findById - Should return PaymentBaseResponse when payment exists")
    void findById_shouldReturnBaseResponse_whenPaymentExists() {
        var savedPayment = PaymentUtils.savedPayment(EXISTING_ID);

        when(paymentRepository.findById(EXISTING_ID)).thenReturn(Optional.of(savedPayment));

        var result = service.findById(EXISTING_ID);

        var expectedResponse = PaymentUtils.asBaseResponse(savedPayment);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(paymentRepository).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("findById - Should throw not-found exception when payment does not exist")
    void findById_shouldThrowNotFound_whenPaymentDoesNotExist() {
        when(paymentRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PAYMENT_NOT_FOUND);

        verify(paymentRepository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll - Should return paged response when payments exist")
    void findAll_shouldReturnPagedResponse_whenPaymentsExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var paymentList = PaymentUtils.paymentList();
        var pagedPayments = PageUtils.toPage(paymentList);

        when(paymentRepository.findAll(pageRequest)).thenReturn(pagedPayments);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedPayments.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedPayments.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedPayments.getNumber());

        var expectedResponse = PaymentUtils.baseResponseList();

        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(paymentRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("findByPatient - should return list of payments when patient exists")
    void findByPatient_shouldReturnPayments_whenPatientExists() {
        var patientId = EXISTING_ID;
        var savedPatient = PatientUtils.savedPatient(patientId);
        var paymentList = PaymentUtils.paymentList();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(savedPatient));
        when(paymentRepository.findByPatientId(patientId)).thenReturn(paymentList);

        var result = service.findByPatient(patientId);

        var expectedResponse = PaymentUtils.baseResponseList();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(patientRepository).findById(patientId);
        verify(paymentRepository).findByPatientId(patientId);
    }

    @Test
    @DisplayName("findByPatient - Should throw not-found exception when patient does not exist")
    void findByPatient_shouldThrowNotFound_whenPatientDoesNotExist() {
        when(patientRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByPatient(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PATIENT_NOT_FOUND);

        verify(patientRepository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("save - Should return base response when request is valid")
    void save_shouldReturnBaseResponse_whenCreatingValidPayment() {
        var cashier = CashierUtils.savedCashier(EXISTING_ID);
        var patient = PatientUtils.savedPatient(EXISTING_ID);
        var savedPayment = PaymentUtils.savedPayment(EXISTING_ID);

        var expectedResponse = PaymentUtils.asBaseResponse(savedPayment);
        var request = PaymentUtils.asBaseRequest();

        when(patientRepository.findById(EXISTING_ID)).thenReturn(Optional.of(patient));
        when(cashierRepository.findById(EXISTING_ID)).thenReturn(Optional.of(cashier));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save - should throw not-found when patient does not exist")
    void save_shouldThrowNotFound_whenPatientDoesNotExist() {
        var request = PaymentUtils.asBaseRequest();

        var expectedErrorMessage = PATIENT_NOT_FOUND;

        doThrow(new ResourceNotFoundException(expectedErrorMessage))
                .when(patientRepository).findById(request.cashierId());

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("save - should throw not-found when cashier does not exist")
    void save_shouldThrowNotFound_whenCashierDoesNotExist() {
        var request = PaymentUtils.asBaseRequest();
        var patient = PatientUtils.savedPatient(EXISTING_ID);

        var expectedErrorMessage = CASHIER_NOT_FOUND;

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        doThrow(new ResourceNotFoundException(expectedErrorMessage))
                .when(cashierRepository).findById(request.cashierId());

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(expectedErrorMessage);
    }
}