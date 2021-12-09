INSERT INTO room (num, phone)
VALUES (100, 100),
       (200, 200),
       (300, 300),
       (400, 400)
ON CONFLICT (id) DO NOTHING;


INSERT INTO specialist (specialization)
VALUES ('Лев'),
       ('Тигр')
ON CONFLICT (id) DO NOTHING;

INSERT INTO doctor (date_of_employment, education, room_id,firstname,lastname)
VALUES ('2021-11-23', 'Высшее', 1,'Петя','Петров'),
       ('2021-11-21', 'Высшее', 1,'Иоанн','Просвещенный')
ON CONFLICT (id) DO NOTHING;

INSERT INTO doctors_specialist (specialist_id, doctor_id)
VALUES (1, 1),
       (1, 2)
ON CONFLICT (specialist_id,doctor_id) DO NOTHING;