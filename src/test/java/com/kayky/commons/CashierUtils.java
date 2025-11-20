package com.kayky.commons;

import com.kayky.domain.cashier.Cashier;
import com.kayky.enums.Gender;

import java.math.BigDecimal;

public class CashierUtils {

    public static Cashier savedCashier(Long id) {
        return Cashier.builder()
                .id(id)
                .firstName("Maria")
                .lastName("Silva")
                .email("maria.silva@example.com")
                .password("password123")
                .gender(Gender.FEMALE)
                .active(true)
                .registrationNumber("REG-001")
                .department("Finance")
                .salary(new BigDecimal("3500.00"))
                .build();
    }
}
