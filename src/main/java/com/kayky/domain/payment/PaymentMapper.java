package com.kayky.domain.payment;

import com.kayky.domain.payment.request.PaymentBaseRequest;
import com.kayky.domain.payment.response.PaymentBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "cashier", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "paymentDate", expression = "java(java.time.LocalDateTime.now())")
    Payment toEntity(PaymentBaseRequest request);

    // Entity -> Response
    @Mapping(target = "patientId", source = "payment.patient.id")
    @Mapping(target = "patientName", expression = "java(payment.getPatient().getFirstName() + \" \" + payment.getPatient().getLastName())")
    @Mapping(target = "cashierId", source = "payment.cashier.id")
    @Mapping(target = "cashierName", expression = "java(payment.getCashier().getFirstName() + \" \" + payment.getCashier().getLastName())")
    PaymentBaseResponse toPaymentBaseResponse(Payment payment);

}
