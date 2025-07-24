package com.kayky.domain.patient;

import com.kayky.domain.user.User;
import com.kayky.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Patient extends User {

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String address;
    private String bloodType;
}
