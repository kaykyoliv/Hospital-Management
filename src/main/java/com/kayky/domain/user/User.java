package com.kayky.domain.user;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public abstract class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private Boolean active = true;

}
