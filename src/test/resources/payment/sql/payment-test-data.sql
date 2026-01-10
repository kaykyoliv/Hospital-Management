INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
    (1, 'Robert', 'Williams', 'robert.williams@example.com', 'hashed_password', 'MALE', true),
    (2, 'John', 'Doe', 'john.doe@example.com', 'hashed_password', 'MALE', true),

    (3, 'Emily', 'Clark', 'emily.clark@example.com', 'hashed_password', 'FEMALE', true),
    (4, 'Jane', 'Smith', 'jane.smith@example.com', 'hashed_password', 'FEMALE', true),

    (5, 'Michael', 'Brown', 'michael.brown@example.com', 'hashed_password', 'MALE', true),
    (6, 'Alice', 'Johnson', 'alice.johnson@example.com', 'hashed_password', 'FEMALE', true);


INSERT INTO tb_employee (id, registration_number, department, salary)
VALUES
    (1, 'REG-001', 'Finance', 18000.00),
    (3, 'REG-003', 'Finance', 15000.00),
    (5, 'REG-005', 'Finance', 20000.00);

INSERT INTO tb_cashier(id) VALUES (1), (3), (5);


INSERT INTO tb_patient (
    id,
    address,
    blood_type
)
VALUES
    (2, 'Street A, 123', 'A+'),
    (4, 'Street B, 456', 'O-'),
    (6, 'Street C, 789', 'B+');

INSERT INTO tb_payment (
    id,
    patient_id,
    cashier_id,
    amount,
    payment_method,
    status,
    payment_date
)
VALUES
    (1, 2, 1, 350.00,  'CREDIT_CARD', 'PAID',    '2026-01-03 14:40:00'),
    (2, 4, 3, 1200.50, 'PIX',         'PAID',    '2026-01-04 11:20:00'),
    (3, 6, 3, 500.00,  'DEBIT_CARD',  'PENDING', '2026-01-04 10:00:00');
