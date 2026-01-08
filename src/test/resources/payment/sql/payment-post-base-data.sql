INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
    (1, 'Robert', 'Williams', 'robert.williams@example.com', 'hashed_password', 'MALE', true),
    (2, 'John', 'Doe', 'john.doe@example.com', 'hashed_password', 'MALE', true);

INSERT INTO tb_employee (id, registration_number, department, salary)
VALUES
    (1, 'REG-001', 'Finance', 18000.00);

INSERT INTO tb_cashier(id) VALUES (1);

INSERT INTO tb_patient (
    id,
    address,
    blood_type
)
VALUES
    (2, 'Street A, 123', 'A+');

