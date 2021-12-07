-- SELECT u.id,u.first_name,u.last_name,AVG(z.rate) AS rating FROM users u
--                                                                     INNER JOIN users_rating z  on u.id = z.users_id
-- GROUP BY u.id, u.first_name, u.last_name

INSERT INTO users (first_name, last_name, role)
VALUES ('Мадина', 'Ленина', 1),
       ('Петр', 'Полянский', 1),
       ('иван', 'Иванов', 0)

ON CONFLICT (id) DO NOTHING;

INSERT INTO doctors_rating (feedback, rate, doctor_id, user_id)
VALUES ('Круто', 5, 1, 1),
       ('Так себе', 2, 1, 1),
       ('Ну такое', 1, 2, 2),
       ('Отлично', 5, 2, 2)

ON CONFLICT (id) DO NOTHING;
