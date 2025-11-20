package com.kayky.domain.cashier;

import com.kayky.domain.employee.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_cashier")
@SuperBuilder
public class Cashier extends Employee {
}