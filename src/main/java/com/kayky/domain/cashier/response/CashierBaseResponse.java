package com.kayky.domain.cashier.response;

import com.kayky.enums.Gender;

import java.math.BigDecimal;

public record CashierBaseResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Gender gender,
        String registrationNumber,
        String department,
        BigDecimal salary,
        Boolean active
) {}
