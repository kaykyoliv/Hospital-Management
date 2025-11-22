package com.kayky.domain.cashier.response;

import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashierBaseResponse(
        @Schema(
                description = "Unique identifier of the cashier",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Cashier first name",
                example = "John"
        )
        String firstName,

        @Schema(
                description = "Cashier last name",
                example = "Doe"
        )
        String lastName,

        @Schema(
                description = "Cashier email address",
                example = "john.doe@hospital.com"
        )
        String email,

        @Schema(
                description = "Cashier gender",
                example = "MALE"
        )
        Gender gender,

        @Schema(
                description = "Internal registration number",
                example = "RG12991"
        )
        String registrationNumber,

        @Schema(
                description = "Department where the cashier works",
                example = "Front Desk"
        )
        String department,

        @Schema(
                description = "Cashier salary",
                example = "2500.00"
        )
        BigDecimal salary,

        @Schema(
                description = "Indicates whether the cashier is active",
                example = "true"
        )
        Boolean active
) {}
