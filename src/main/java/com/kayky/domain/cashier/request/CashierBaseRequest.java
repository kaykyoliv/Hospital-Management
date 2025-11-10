package com.kayky.domain.cashier.request;

import com.kayky.enums.Gender;

import java.math.BigDecimal;

public record CashierBaseRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Gender gender,
        String registrationNumber,
        String department,
        BigDecimal salary
) {}
