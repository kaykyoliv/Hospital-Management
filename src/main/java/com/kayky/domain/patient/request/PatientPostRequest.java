package com.kayky.domain.patient.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.enums.Gender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PatientPostRequest extends UserBaseRequest {
    private Gender gender;
    private String address;
    private String bloodType;
}
