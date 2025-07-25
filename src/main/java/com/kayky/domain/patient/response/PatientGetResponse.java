package com.kayky.domain.patient.response;

import com.kayky.domain.user.response.UserGetResponse;
import com.kayky.enums.Gender;

public record PatientGetResponse(
        UserGetResponse user,
        Gender gender,
        String address,
        String bloodType
) {}
