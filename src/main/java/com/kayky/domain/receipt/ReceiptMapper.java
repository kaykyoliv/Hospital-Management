package com.kayky.domain.receipt;

import com.kayky.domain.receipt.response.ReceiptBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {


    @Mapping(target = "cashierName", source = "cashier.firstName")
    @Mapping(target = "patientName", source = "patient.firstName")
    ReceiptBaseResponse toReceiptBaseResponse(Receipt receipt);
}
