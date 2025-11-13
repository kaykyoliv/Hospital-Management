package com.kayky.domain.employee.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.domain.user.response.UserBaseResponse;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class EmployeeRequest extends UserBaseRequest {

    @NotBlank(message = "Registration number is required")
    @Size(max = 20, message = "Registration number must have at most 20 characters")
    private String registrationNumber;

    @NotBlank(message = "Department is required")
    @Size(max = 50, message = "Department must have at most 50 characters")
    private String department;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Salary must have at most 2 decimal places")
    private BigDecimal salary;
}
