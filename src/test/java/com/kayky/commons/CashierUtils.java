package com.kayky.commons;

import com.kayky.domain.cashier.Cashier;
import com.kayky.domain.cashier.request.CashierBaseRequest;
import com.kayky.domain.cashier.response.CashierBaseResponse;
import com.kayky.enums.Gender;

import java.math.BigDecimal;
import java.util.List;

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

    public static List<Cashier> cashierList() {
        return List.of(
                Cashier.builder()
                        .id(1L)
                        .firstName("Maria")
                        .lastName("Silva")
                        .email("maria.silva@example.com")
                        .password("password123")
                        .gender(Gender.FEMALE)
                        .active(true)
                        .registrationNumber("REG-001")
                        .department("Finance")
                        .salary(new BigDecimal("3500.00"))
                        .build(),

                Cashier.builder()
                        .id(2L)
                        .firstName("João")
                        .lastName("Pereira")
                        .email("joao.pereira@example.com")
                        .password("password123")
                        .gender(Gender.MALE)
                        .active(true)
                        .registrationNumber("REG-002")
                        .department("Front Desk")
                        .salary(new BigDecimal("3200.00"))
                        .build(),

                Cashier.builder()
                        .id(3L)
                        .firstName("Ana")
                        .lastName("Souza")
                        .email("ana.souza@example.com")
                        .password("password123")
                        .gender(Gender.FEMALE)
                        .active(true)
                        .registrationNumber("REG-003")
                        .department("Billing")
                        .salary(new BigDecimal("3400.00"))
                        .build()
        );
    }

    public static CashierBaseResponse asBaseResponse(Cashier cashier) {
        return CashierBaseResponse.builder()
                .id(cashier.getId())
                .firstName(cashier.getFirstName())
                .lastName(cashier.getLastName())
                .email(cashier.getEmail())
                .gender(cashier.getGender())
                .active(cashier.getActive())
                .registrationNumber(cashier.getRegistrationNumber())
                .department(cashier.getDepartment())
                .salary(cashier.getSalary())
                .build();
    }

    public static List<CashierBaseResponse> baseResponseList(){
        return cashierList().stream().map(CashierUtils::asBaseResponse).toList();
    }

    public static CashierBaseRequest asBaseRequest() {
        return CashierBaseRequest.builder()
                .firstName("Maria")
                .lastName("Silva")
                .email("maria.silva@example.com")
                .password("password123")
                .gender(Gender.FEMALE)
                .registrationNumber("REG-001")
                .department("Finance")
                .salary(new BigDecimal("3500.00"))
                .build();
    }
}
