package com.kayky.domain.patient.response;

import com.kayky.domain.user.response.UserBaseResponse;
import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientGetResponse extends UserBaseResponse {
    @Schema(description = "Patient's gender", example = "MALE")
    private Gender gender;

    @Schema(description = "Patient's full address", example = "123 Main Street, Apt 4B", maxLength = 200)
    private String address;

    @Schema(description = "Patient's blood type", example = "O+", maxLength = 3)
    private String bloodType;
}
