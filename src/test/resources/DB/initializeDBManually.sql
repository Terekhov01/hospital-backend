INSERT INTO roles VALUES(0, 'ROLE_USER');
INSERT INTO roles VALUES(1, 'ROLE_PATIENT');
INSERT INTO roles VALUES(2, 'ROLE_DOCTOR');
INSERT INTO roles VALUES(3, 'ROLE_ADMIN');

UPDATE user_roles SET role_id = 1 WHERE user_id = 1;
UPDATE user_roles SET role_id = 2 WHERE user_id = 2;
UPDATE user_roles SET role_id = 3 WHERE user_id = 3;
UPDATE user_roles SET role_id = 2 WHERE user_id = 4;
UPDATE user_roles SET role_id = 2 WHERE user_id = 5;

SELECT user_roles.*, users.id, users.first_name FROM user_roles JOIN users ON user_roles.user_id = users.id;

INSERT INTO room VALUES (0, 0, '8-900-000-00-00');
INSERT INTO room VALUES (1, 1, '8-911-111-11-11');

INSERT INTO doctor VALUES (0, '1999-01-08', 'Linux > windows', 0, 2);
INSERT INTO doctor VALUES (1, '1993-12-11', 'Geese are really cool!', 0, 4);
INSERT INTO doctor VALUES (2, '1993-12-11', 'Geese are really cool!', 1, 5);

INSERT INTO doctor_schedule VALUES (0, 0);

INSERT INTO specialist VALUES (0, 'Goose');
INSERT INTO specialist VALUES (1, 'Gosling');
INSERT INTO specialist VALUES (2, 'Gander');

INSERT INTO doctors_specialist VALUES (0, 0);
INSERT INTO doctors_specialist VALUES (0, 2);
INSERT INTO doctors_specialist VALUES (1, 1);
INSERT INTO doctors_specialist VALUES (2, 1);
INSERT INTO doctors_specialist VALUES (2, 2);