package com.kayky.domain.cashier;

import com.kayky.domain.cashier.response.CashierBaseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashierMapper {

    CashierBaseResponse toCashierBaseResponse(Cashier cashier);
}
