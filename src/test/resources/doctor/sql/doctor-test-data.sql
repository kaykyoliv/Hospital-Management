INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
(1, 'Robert', 'Williams', 'robert.williams@example.com', '123456', 'MALE', true),
(2, 'Emily', 'Johnson', 'emily.johnson@example.com', '123456', 'FEMALE', true),
(3, 'Michael', 'Brown', 'michael.brown@example.com', '123456', 'MALE', true);

INSERT INTO tb_employee (id, registration_number, department, salary)
VALUES
(1, 'REG-001', 'Cardiology', 18000.00),
(2, 'REG-002', 'Dermatology', 15000.00),
(3, 'REG-003', 'Neurology', 20000.00);

INSERT INTO tb_doctor (id, specialty, crm, phone_number, office_number, availability)
VALUES
(1, 'Cardiology',  'CRM-12345', '+55 19 99999-0001', 'Room 101', true),
(2, 'Dermatology', 'CRM-67890', '+55 11 98888-0002', 'Room 102', true),
(3, 'Neurology',   'CRM-54321', '+55 21 97777-0003', 'Room 103', false);