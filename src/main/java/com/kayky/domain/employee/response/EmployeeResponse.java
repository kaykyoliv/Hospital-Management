package com.kayky.domain.employee.response;

import com.kayky.domain.user.response.UserBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Base response containing employee-related information")
public abstract class EmployeeResponse extends UserBaseResponse {

    @Schema(
            description = "Unique registration identifier of the employee",
            example = "REG-2025-991"
    )
    private String registrationNumber;

    @Schema(
            description = "Department assigned to the employee",
            example = "Front Desk"
    )
    private String department;

    @Schema(
            description = "Employee salary amount",
            example = "3500.00"
    )
    private BigDecimal salary;
}
