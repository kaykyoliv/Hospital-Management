package com.kayky.domain.doctor;

import com.kayky.domain.user.User;
import com.kayky.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_doctor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Doctor extends User {

    private String specialty;

    @Column(nullable = false, unique = true)
    private String crm;

    private String phoneNumber;

    private String officeNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean availability = true;
}
