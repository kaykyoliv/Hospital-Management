package com.kayky.domain.employee.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.domain.user.response.UserBaseResponse;
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

    private String registrationNumber;
    private String department;
    private BigDecimal salary;
}
