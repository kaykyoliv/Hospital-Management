package com.kayky.domain.patient.response;

import com.kayky.domain.user.response.UserBaseResponse;
import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientPostResponse extends UserBaseResponse {
    @Schema(description = "Patient's gender", example = "MALE")
    private Gender gender;

    @Schema(description = "Patient's full address", example = "123 Main Street, Apt 4B", maxLength = 200)
    private String address;

    @Schema(description = "Patient's blood type", example = "O+", maxLength = 3)
    private String bloodType;
}
