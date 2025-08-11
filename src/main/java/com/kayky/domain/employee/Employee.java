package com.kayky.domain.employee;

import com.kayky.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_employee")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public abstract class Employee extends User {

    @Column(nullable = false)
    private String registrationNumber;
    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private BigDecimal salary;
}