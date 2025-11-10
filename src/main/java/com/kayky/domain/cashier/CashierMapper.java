package com.kayky.domain.cashier;

import com.kayky.domain.cashier.request.CashierBaseRequest;
import com.kayky.domain.cashier.response.CashierBaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CashierMapper {

    Cashier toEntity(CashierBaseRequest postRequest);

    CashierBaseResponse toCashierBaseResponse(Cashier cashier);

    void updateCashierFromRequest(CashierBaseRequest putRequest, @MappingTarget Cashier cashier);
}
