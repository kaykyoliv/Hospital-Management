package com.kayky.domain.doctor.response;

import com.kayky.domain.user.response.UserBaseResponse;
import com.kayky.enums.Gender;
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
public class DoctorPostResponse extends UserBaseResponse {
    @Schema(description = "Doctor specialty", example = "Cardiology")
    private String specialty;

    @Schema(description = "Doctor CRM registration number", example = "123456")
    private String crm;

    @Schema(description = "Phone number", example = "+5511987654321")
    private String phoneNumber;

    @Schema(description = "Office number", example = "101")
    private String officeNumber;

    @Schema(description = "Gender of the doctor", example = "MALE")
    private Gender gender;

    @Schema(description = "Availability status", example = "true")
    private Boolean availability;
}
