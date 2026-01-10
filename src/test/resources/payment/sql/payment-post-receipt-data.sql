INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
    (1, 'Robert', 'Williams', 'robert.williams@example.com', 'hashed_password', 'MALE', true),
    (2, 'John', 'Doe', 'john.doe@example.com', 'hashed_password', 'MALE', true),
    (3, 'Emily', 'Clark', 'emily.clark@example.com', 'hashed_password', 'FEMALE', true);

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
    (1, 2, 1, 350.00,  'CREDIT_CARD', 'PAID', '2026-01-03 14:40:00');

INSERT INTO tb_receipt(id, payment_id, cashier_id, patient_id, issued_at, receipt_number, total_amount)
VALUES
    (1, 1, 1, 2, '2027-01-10 12:00:00', 'RCT-ABC12345', 350.00);
