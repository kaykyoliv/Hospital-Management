package com.kayky.domain.doctor;

import com.kayky.domain.employee.Employee;
import com.kayky.enums.Gender;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Doctor", description = "Operations related to doctor management")
public class Doctor extends Employee {

    private String specialty;

    @Column(nullable = false, unique = true)
    private String crm;

    private String phoneNumber;

    private String officeNumber;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean availability = true;
}
