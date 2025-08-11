package com.kayky.domain.employee.response;

import com.kayky.domain.user.response.UserBaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class EmployeeResponse extends UserBaseResponse {

    private String registrationNumber;
    private String department;
    private BigDecimal salary;
}
