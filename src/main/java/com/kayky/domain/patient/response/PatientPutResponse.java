package com.kayky.domain.patient.response;

import com.kayky.domain.user.response.UserBaseResponse;
import com.kayky.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientPutResponse extends UserBaseResponse {
    private Gender gender;
    private String address;
    private String bloodType;
}
