package com.kayky.domain.patient.response;

import com.kayky.domain.user.response.UserBaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientBaseResponse extends UserBaseResponse {

    @Schema(description = "Patient's full address", example = "123 Main Street, Apt 4B", maxLength = 200)
    private String address;

    @Schema(description = "Patient's blood type", example = "O+", maxLength = 3)
    private String bloodType;
}
