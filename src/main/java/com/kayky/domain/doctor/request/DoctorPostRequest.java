package com.kayky.domain.doctor.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DoctorPostRequest extends UserBaseRequest {

    @Schema(description = "Doctor specialty", example = "Cardiology")
    @NotBlank(message = "Specialty is required")
    private String specialty;

    @Schema(description = "Doctor CRM registration number", example = "123456")
    @NotBlank(message = "CRM is required")
    @Size(max = 20, message = "CRM must be at most 20 characters")
    private String crm;

    @Schema(description = "Phone number", example = "+5511987654321")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+55\\d{10,11}$", message = "Phone number must be valid")
    private String phoneNumber;

    @Schema(description = "Office number", example = "101")
    @NotBlank(message = "Office number is required")
    private String officeNumber;

    @Schema(description = "Gender of the doctor", example = "MALE")
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Schema(description = "Availability status", example = "true")
    @NotNull(message = "Availability is required")
    private Boolean availability;
}
