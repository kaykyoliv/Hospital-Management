package com.kayky.domain.doctor.request;

import com.kayky.domain.user.request.UserBaseRequest;
import com.kayky.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DoctorPutRequest extends UserBaseRequest {
    private String crm;
    private String phoneNumber;
    private String officeNumber;
    private Gender gender;
    private Boolean availability;
}
