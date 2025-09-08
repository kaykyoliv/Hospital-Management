package com.kayky.domain.doctor.request;

import com.kayky.domain.employee.request.EmployeeRequest;
import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DoctorBaseRequest extends EmployeeRequest {

    @Schema(description = "Doctor specialty", example = "Cardiology")
    @NotBlank(message = "Specialty is required")
    private String specialty;

    @Schema(description = "Doctor CRM registration number", example = "123456")
    @NotBlank(message = "CRM is required")
    @Size(max = 20, message = "CRM must be at most 20 characters")
    private String crm;

    @Schema(description = "Phone number", example = "+55 11 98765-4321")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+55\\s\\d{2}\\s\\d{4,5}-\\d{4}$", message = "Phone number must be valid")
    private String phoneNumber;

    @Schema(description = "Office number", example = "101")
    @NotBlank(message = "Office number is required")
    private String officeNumber;

    @Schema(description = "Availability status", example = "true")
    @NotNull(message = "Availability is required")
    private Boolean availability;
}
