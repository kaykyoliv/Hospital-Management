INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
  (1, 'Robert', 'Williams', 'robert.williams@example.com', 'hashed', 'MALE', TRUE),
  (2, 'John', 'Doe', 'john.doe@example.com', 'hashed', 'MALE', TRUE),
  (3, 'Emily', 'Clark', 'emily.clark@example.com', 'hashed_password', 'FEMALE', TRUE),
  (4, 'Jane', 'Smith', 'jane.smith@example.com', 'hashed_password', 'FEMALE', TRUE);

INSERT INTO tb_employee (id, registration_number, department, salary)
VALUES
  (1, 'REG-1001', 'Cardiology', 25000.00),
  (3, 'REG-1002', 'Orthopedics', 22000.00);

INSERT INTO tb_doctor (id, specialty, crm, phone_number, office_number, availability)
VALUES
  (1, 'Cardiology', 'CRM-1001', '+55 11 99999-0001', 'Room 101', TRUE),
  (3, 'Orthopedics', 'CRM-1002', '+55 11 99999-0002', 'Room 202', TRUE);

INSERT INTO tb_patient (id, address, blood_type)
VALUES
  (2, 'Street A, 123', 'A+'),
  (4, 'Street B, 456', 'O-');

INSERT INTO tb_operation (id, description, scheduled_at, doctor_id, patient_id, status)
VALUES(1, 'Cirurgia cardíaca de alta complexidade', '2025-09-10 14:30:00', 1, 2, 'SCHEDULED')