INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
(1, 'Robert', 'Williams', 'robert.williams@example.com', '123456', 'MALE', true),
(2, 'Emily', 'Johnson', 'emily.johnson@example.com', '123456', 'FEMALE', true),
(3, 'Michael', 'Brown', 'michael.brown@example.com', '123456', 'MALE', true);

INSERT INTO tb_employee (id, registration_number, department, salary)
VALUES
(1, 'REG-001', 'Finance', 18000.00),
(2, 'REG-002', 'Finance', 15000.00),
(3, 'REG-003', 'Finance', 20000.00);

INSERT INTO tb_cashier(id) VALUES (1), (2), (3);
