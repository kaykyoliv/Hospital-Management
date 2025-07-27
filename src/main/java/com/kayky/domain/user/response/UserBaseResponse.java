package com.kayky.domain.user.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserBaseResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
