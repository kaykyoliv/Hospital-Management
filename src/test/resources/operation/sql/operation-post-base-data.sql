-- 1. Usuários base
INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
  (1, 'Robert', 'Williams', 'robert.williams@example.com', 'hashed', 'MALE', TRUE),
  (2, 'John', 'Doe', 'john.doe@example.com', 'hashed', 'MALE', TRUE);

-- 2. Employee (obrigatório para Doctor)
INSERT INTO tb_employee (id, registration_number, department, salary)
VALUES
  (1, 'REG-1001', 'Cardiology', 25000.00);

-- 3. Doctor completo
INSERT INTO tb_doctor (id, specialty, crm, phone_number, office_number, availability)
VALUES
  (1, 'Cardiology', 'CRM-1001', '+55 11 99999-0001', 'Room 101', TRUE);

-- 4. Patient
INSERT INTO tb_patient (id, address, blood_type)
VALUES
  (2, 'Street A, 123', 'A+');