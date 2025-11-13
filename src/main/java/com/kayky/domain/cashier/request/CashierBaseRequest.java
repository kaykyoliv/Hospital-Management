package com.kayky.domain.cashier.request;

import com.kayky.enums.Gender;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CashierBaseRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must have at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must have at most 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
        String password,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotBlank(message = "Registration number is required")
        @Size(max = 20, message = "Registration number must have at most 20 characters")
        String registrationNumber,

        @NotBlank(message = "Department is required")
        @Size(max = 50, message = "Department must have at most 50 characters")
        String department,

        @NotNull(message = "Salary is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
        BigDecimal salary
) {}
