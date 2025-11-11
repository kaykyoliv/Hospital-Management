package com.kayky.domain.payment;

import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.cashier.CashierRepository;
import com.kayky.domain.patient.PatientRepository;
import com.kayky.domain.payment.request.PaymentBaseRequest;
import com.kayky.domain.payment.response.PaymentBaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final CashierRepository cashierRepository;
    private final PaymentMapper mapper;

    @Transactional(readOnly = true)
    public PaymentBaseResponse findById(Long id) {
        return paymentRepository.findById(id)
                .map(mapper::toPaymentBaseResponse)
                .orElseThrow(() -> {
                    log.warn("Payment not found with ID {}", id);

                    return new ResourceNotFoundException("Payment not found");
                });
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentBaseResponse> findAll(Pageable pageable) {
        var paginatedPayments = paymentRepository.findAll(pageable);
        return PageUtils.mapPage(paginatedPayments, mapper::toPaymentBaseResponse);
    }

    @Transactional(readOnly = true)
    public List<PaymentBaseResponse> findByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return paymentRepository.findByPatientId(patientId).stream()
                .map(mapper::toPaymentBaseResponse)
                .toList();
    }

    @Transactional
    public PaymentBaseResponse save(PaymentBaseRequest request) {
       var patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

       var cashier = cashierRepository.findById(request.cashierId())
                .orElseThrow(() -> new ResourceNotFoundException("Cashier not found"));

        var paymentToSave = mapper.toEntity(request);

        paymentToSave.setPatient(patient);
        paymentToSave.setCashier(cashier);

        var savedPayment = paymentRepository.save(paymentToSave);

        return mapper.toPaymentBaseResponse(savedPayment);
    }


}
