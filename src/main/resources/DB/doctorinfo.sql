INSERT INTO room (num, phone)
VALUES (100, 100),
       (101, 101),
       (102, 102),
       (103, 103),
       (104, 104),
       (105, 105),
       (106, 106),
       (107, 107),
       (108, 108),
       (109, 109),
       (110, 110),
       (200, 200),
       (201, 201),
       (202, 202),
       (203, 203),
       (204, 204),
       (205, 205),
       (206, 206),
       (207, 207),
       (208, 208),
       (209, 209),
       (210, 210),
       (300, 300),
       (301, 301),
       (302, 302),
       (303, 303),
       (304, 304),
       (305, 305),
       (306, 306),
       (307, 307),
       (308, 308),
       (309, 309),
       (310, 310),
       (400, 400),
       (401, 401),
       (402, 402),
       (403, 403),
       (404, 404),
       (405, 405),
       (406, 406),
       (407, 407),
       (408, 408),
       (409, 409),
       (410, 410),
       (500, 500),
       (501, 501),
       (502, 502),
       (503, 503),
       (504, 504),
       (505, 505),
       (506, 506),
       (507, 507),
       (508, 508),
       (509, 509),
       (510, 510)

ON CONFLICT (id) DO NOTHING;

INSERT INTO specialist (specialization)
VALUES ('Окулист'),
       ('Терапевт'),
       ('Психолог'),
       ('Хирург'),
       ('Реаниматолог'),
       ('Аллерголог'),
       ('Анестезиолог'),
       ('Диетолог'),
       ('Иммунолог'),
       ('Кардиолог'),
       ('Инфекционист'),
       ('Косметолог'),
       ('Нарколог'),
       ('Невролог'),
       ('Ортопед')

ON CONFLICT (id) DO NOTHING;

-- INSERT INTO doctor (date_of_employment, education, room_id,firstname,lastname)
-- VALUES ('2021-11-23', 'Высшее', 1,'Петя','Петров'),
--        ('2021-11-21', 'Высшее', 1,'Иоанн','Просвещенный')
-- ON CONFLICT (id) DO NOTHING;
--
-- INSERT INTO doctors_specialist (specialist_id, doctor_id)
-- VALUES (1, 1),
--        (1, 2)
-- ON CONFLICT (specialist_id,doctor_id) DO NOTHING;

-- SELECT date_trunc('month', start_date_time) AS start_month, count(appointment_registration_id) as monthly_sum
-- FROM appointment_registration WHERE appointment_registration.doctor =:doctor
-- GROUP BY date_trunc('month', start_date_time);