package com.kayky.domain.user.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserBaseRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
