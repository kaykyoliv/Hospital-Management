package com.kayky.domain.doctor.response;

import com.kayky.domain.user.response.UserBaseResponse;
import com.kayky.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DoctorGetResponse extends UserBaseResponse {

    private String specialty;
    private String crm;
    private String phoneNumber;
    private String officeNumber;
    private Gender gender;
    private Boolean availability;
}
