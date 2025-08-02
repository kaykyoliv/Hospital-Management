package com.kayky.domain.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UserBaseRequest {

    @Schema(description = "User's first name", example = "John", maxLength = 50)
    @NotBlank(message = "First name must not be blank")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "John", maxLength = 50)
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com", maxLength = 100)
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Schema(description = "User's password (between 6 and 20 characters)", example = "secure123", minLength = 6, maxLength = 100)
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;
}
