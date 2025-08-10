package com.kayky.domain.doctor.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.enums.Gender;
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
public class DoctorPutRequest extends UserBaseRequest {
    @NotBlank(message = "CRM is required")
    @Size(max = 20, message = "CRM must be at most 20 characters")
    private String crm;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+55\\d{10,11}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Office number is required")
    private String officeNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Availability is required")
    private Boolean availability;
}
