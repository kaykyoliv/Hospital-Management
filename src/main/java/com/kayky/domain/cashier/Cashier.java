package com.kayky.domain.cashier;

import com.kayky.domain.employee.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cashier")
public class Cashier extends Employee {
}