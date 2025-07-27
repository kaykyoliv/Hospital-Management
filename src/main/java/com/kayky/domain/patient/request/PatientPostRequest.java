package com.kayky.domain.patient.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PatientPostRequest extends UserBaseRequest {
    @NotNull(message = "Gender must be provided")
    private Gender gender;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 200, message = "Address must be at most 200 characters")
    private String address;

    @NotBlank(message = "Blood type must not be blank")
    @Size(max = 3, message = "Blood type must be at most 3 characters")
    private String bloodType;
}
