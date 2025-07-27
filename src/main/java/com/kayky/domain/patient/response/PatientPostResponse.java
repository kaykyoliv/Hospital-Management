package com.kayky.domain.patient.response;

import com.kayky.domain.user.response.UserBaseResponse;
import com.kayky.enums.Gender;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientPostResponse extends UserBaseResponse {
    private Gender gender;
    private String address;
    private String bloodType;
}
