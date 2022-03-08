INSERT INTO roles VALUES(0, 'ROLE_USER');
INSERT INTO roles VALUES(1, 'ROLE_PATIENT');
INSERT INTO roles VALUES(2, 'ROLE_DOCTOR');
INSERT INTO roles VALUES(3, 'ROLE_ADMIN');

-- Создание пользователя происходит через графический интерфейс (сайт)

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

-- Просмотр всех докторов
SELECT users.id, users.first_name, users.patronymic, users.last_name, doctor.id, doctor.date_of_employment, doctor.education FROM users JOIN doctor ON users.id = doctor.user_id;

INSERT INTO doctor_schedule VALUES (0, 0);
INSERT INTO doctor_schedule VALUES (1, 2);

-- Заполнение расписания происходит через графический инерфейс

INSERT INTO specialist VALUES (0, 'Goose');
INSERT INTO specialist VALUES (1, 'Gosling');
INSERT INTO specialist VALUES (2, 'Gander');

INSERT INTO doctors_specialist VALUES (0, 0);
INSERT INTO doctors_specialist VALUES (0, 2);
INSERT INTO doctors_specialist VALUES (1, 1);
INSERT INTO doctors_specialist VALUES (2, 1);
INSERT INTO doctors_specialist VALUES (2, 2);

-- In following queries change constraint names to the appropriate ones
alter table file
drop constraint fkhihf7pbm68htfxa9f6jlkfkcx,
add constraint fkhihf7pbm68htfxa9f6jlkfkcx
   foreign key (appointment)
   references appointment(appointment_id)
   on delete cascade;

-- User (doctor) cascade drop below

alter table doctors_specialist
drop constraint fknf0a62urce3os299le9w44lf7,
add constraint fknf0a62urce3os299le9w44lf7
 foreign key (doctor_id)
 references doctor(id)
 on delete cascade;

 alter table doctor
 drop constraint fk11wrxiolc8qa2e64s32xc2yy4,
 add constraint fk11wrxiolc8qa2e64s32xc2yy4
    foreign key (user_id)
    references users(id)
    on delete cascade;

alter table user_roles
drop constraint fkhfh9dx7w3ubf1co1vdev94g3f,
add constraint fkhfh9dx7w3ubf1co1vdev94g3f
   foreign key (user_id)
   references users(id)
   on delete cascade;

alter table doctor_schedule
drop constraint fkrresxag4ex638q3fincrya0wr,
add constraint fkrresxag4ex638q3fincrya0wr
  foreign key (doctor_id)
  references doctor(id)
  on delete cascade;

alter table doctors_specialist
drop constraint fknf0a62urce3os299le9w44lf7,
add constraint fknf0a62urce3os299le9w44lf7
 foreign key (doctor_id)
 references doctor(id)
 on delete cascade;

