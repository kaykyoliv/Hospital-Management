INSERT INTO tb_user (
    id, first_name, last_name, email, password, gender, active
) VALUES
(1, 'Robert',  'Williams',     'robert.williams@example.com',  'hashed_password_1', 'MALE',   true),
(2, 'Jane',  'Smith',   'jane.smith@example.com', 'hashed_password_2', 'FEMALE', true),
(3, 'Alice', 'Johnson', 'alice.johnson@example.com', 'hashed_password_3', 'FEMALE', false);

INSERT INTO tb_patient (
    id, address, blood_type
) VALUES
(1, '101 Oak Lane, Newtown',   'AB-'),
(2, '456 Oak Ave, Somecity',  'AB-'),
(3, '789 Pine Rd, Othercity', 'AB-');
