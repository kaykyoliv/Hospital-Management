package com.kayky.domain.doctor.response;

import com.kayky.domain.employee.response.EmployeeResponse;
import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DoctorBaseResponse extends EmployeeResponse {

    @Schema(description = "Doctor specialty", example = "Cardiology")
    private String specialty;

    @Schema(description = "Doctor CRM registration number", example = "123456")
    private String crm;

    @Schema(description = "Phone number", example = "+5511987654321")
    private String phoneNumber;

    @Schema(description = "Office number", example = "101")
    private String officeNumber;

    @Schema(description = "Availability status", example = "true")
    private Boolean availability;
}
