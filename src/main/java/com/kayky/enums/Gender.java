package com.kayky.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum Gender {
    @Schema(description = "Male gender", example = "MALE")
    MALE,

    @Schema(description = "Female gender", example = "FEMALE")
    FEMALE,

    @Schema(description = "Other gender or non-binary", example = "OTHER")
    OTHER
}
