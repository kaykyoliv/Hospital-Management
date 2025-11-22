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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

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
    @DisplayName("findById: Should return PaymentBaseResponse when the payment exists")
    void findById_ShouldReturnPaymentBaseResponse_WhenPaymentExists() {
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
    @DisplayName("findById: Should throw ResourceNotFoundException when the Payment does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenPaymentDoesNotExist() {

        when(paymentRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PAYMENT_NOT_FOUND);

        verify(paymentRepository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll: Should return PageResponse when payments exist")
    void findAll_ShouldReturnPageResponse_WhenPaymentExist() {
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
    @DisplayName("findByPatient: Should return List<PaymentBaseResponse> when payments exist for the patient")
    void findByPatient_ShouldReturnListOfPaymentBaseResponse_WhenPaymentsExistForPatient() {
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
    @DisplayName("findByPatient: Should throw ResourceNotFoundException when the patient does not exist")
    void findByPatient_ShouldThrowResourceNotFoundException_WhenPatientDoesNotExist() {
        when(patientRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByPatient(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(PATIENT_NOT_FOUND);

        verify(patientRepository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("save: should return PaymentBaseResponse when data is valid")
    void save_ShouldReturnPaymentBaseResponse_WhenDataIsValid() {
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

    @ParameterizedTest(name = "save: should throw ResourceNotFoundException when {0} does not exist")
    @MethodSource("provideNonExistingTypes")
    void save_ShouldThrowResourceNotFoundException_WhenNonExistingType(String missingType) {
        var request = PaymentUtils.asBaseRequest();

        if (missingType.equals("Patient")) {
            when(patientRepository.findById(request.patientId()))
                    .thenReturn(Optional.empty());
        }

        if (missingType.equals("Cashier")) {
            when(patientRepository.findById(request.patientId()))
                    .thenReturn(Optional.of(PatientUtils.savedPatient(1L)));

            when(cashierRepository.findById(request.cashierId()))
                    .thenReturn(Optional.empty());
        }

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> service.save(request))
                .withMessage(missingType.equals("Patient") ? PATIENT_NOT_FOUND : CASHIER_NOT_FOUND);
    }

    private static Stream<String> provideNonExistingTypes() {
        return Stream.of("Patient", "Cashier");
    }

}