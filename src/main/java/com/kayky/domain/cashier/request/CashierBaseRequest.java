package com.kayky.domain.cashier.request;

import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashierBaseRequest(

        @Schema(description = "Cashier first name", example = "John")
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must have at most 50 characters")
        String firstName,

        @Schema(description = "Cashier last name", example = "Doe")
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must have at most 50 characters")
        String lastName,

        @Schema(description = "Cashier email address", example = "john.doe@hospital.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Password that the cashier will use to access the system", example = "strongPass123")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
        String password,

        @Schema(description = "Cashier gender", example = "MALE")
        @NotNull(message = "Gender is required")
        Gender gender,

        @Schema(description = "Internal registration number associated with the cashier", example = "RG12991")
        @NotBlank(message = "Registration number is required")
        @Size(max = 20, message = "Registration number must have at most 20 characters")
        String registrationNumber,


        @Schema(description = "Department where the cashier works", example = "Front Desk")
        @NotBlank(message = "Department is required")
        @Size(max = 50, message = "Department must have at most 50 characters")
        String department,

        @Schema(description = "Cashier salary", example = "2500.00")
        @NotNull(message = "Salary is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
        BigDecimal salary
) {}
