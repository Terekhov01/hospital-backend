INSERT INTO roles VALUES(0, 'ROLE_USER');
INSERT INTO roles VALUES(1, 'ROLE_PATIENT');
INSERT INTO roles VALUES(2, 'ROLE_DOCTOR');
INSERT INTO roles VALUES(3, 'ROLE_ADMIN');

SELECT user_roles.*, users.id, users.first_name FROM user_roles JOIN users ON user_roles.user_id = users.id;

UPDATE user_roles SET role_id = 1 WHERE user_id = 1;
UPDATE user_roles SET role_id = 2 WHERE user_id = 2;
UPDATE user_roles SET role_id = 3 WHERE user_id = 3;

INSERT INTO room VALUES (0, 0, '8-900-000-00-00');
INSERT INTO doctor VALUES (0, '1999-01-08', 'Linux > windows', 0, 2);
INSERT INTO doctor_schedule VALUES (0, 0);