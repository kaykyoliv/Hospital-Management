package com.kayky.domain.patient.request;


import com.kayky.domain.user.request.UserBaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PatientBaseRequest extends UserBaseRequest {

    @Schema(description = "Patient's full address", example = "123 Main Street, Apt 4B", maxLength = 200)
    @NotBlank(message = "Address must not be blank")
    @Size(max = 200, message = "Address must be at most 200 characters")
    private String address;

    @Schema(description = "Patient's blood type", example = "O+", maxLength = 3)
    @NotBlank(message = "Blood type must not be blank")
    @Size(max = 3, message = "Blood type must be at most 3 characters")
    private String bloodType;
}
