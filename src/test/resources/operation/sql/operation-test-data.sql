-- =========================
-- USERS
-- =========================

INSERT INTO tb_user (id, first_name, last_name, email, password, gender, active)
VALUES
    (1, 'Robert', 'Williams', 'robert.williams@example.com', 'hashed_password', 'MALE', true),
    (2, 'John', 'Doe', 'john.doe@example.com', 'hashed_password', 'MALE', true),

    (3, 'Emily', 'Clark', 'emily.clark@example.com', 'hashed_password', 'FEMALE', true),
    (4, 'Jane', 'Smith', 'jane.smith@example.com', 'hashed_password', 'FEMALE', true),

    (5, 'Michael', 'Brown', 'michael.brown@example.com', 'hashed_password', 'MALE', true),
    (6, 'Alice', 'Johnson', 'alice.johnson@example.com', 'hashed_password', 'FEMALE', true);

-- =========================
-- EMPLOYEES (mandatory fields)
-- =========================

INSERT INTO tb_employee (
    id,
    registration_number,
    department,
    salary
)
VALUES
    (1, 'REG-1001', 'Cardiology', 25000.00),
    (3, 'REG-1002', 'Orthopedics', 22000.00),
    (5, 'REG-1003', 'Neurology', 28000.00);

-- =========================
-- DOCTORS
-- =========================

INSERT INTO tb_doctor (
    id,
    specialty,
    crm,
    phone_number,
    office_number,
    availability
)
VALUES
    (1, 'Cardiology', 'CRM-1001', '+55 11 99999-0001', 'Room 101', true),
    (3, 'Orthopedics', 'CRM-1002', '+55 11 99999-0002', 'Room 202', true),
    (5, 'Neurology', 'CRM-1003', '+55 11 99999-0003', 'Room 303', true);

-- =========================
-- PATIENTS
-- =========================

INSERT INTO tb_patient (
    id,
    address,
    blood_type
)
VALUES
    (2, 'Street A, 123', 'A+'),
    (4, 'Street B, 456', 'O-'),
    (6, 'Street C, 789', 'B+');

-- =========================
-- OPERATIONS
-- =========================

INSERT INTO tb_operation (
    id,
    description,
    scheduled_at,
    doctor_id,
    patient_id,
    status
)
VALUES
    (
        1,
        'Cirurgia cardíaca de alta complexidade',
        '2025-09-10 14:30:00',
        1,
        2,
        'SCHEDULED'
    ),
    (
        2,
        'Cirurgia ortopédica de joelho',
        '2025-10-15 09:00:00',
        3,
        4,
        'IN_PROGRESS'
    ),
    (
        3,
        'Cirurgia neurológica de alta precisão',
        '2025-11-20 16:45:00',
        5,
        6,
        'CANCELED'
    );
