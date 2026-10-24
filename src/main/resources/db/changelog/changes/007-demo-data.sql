--liquibase formatted sql

--changeset codex:007-demo-data
-- Demo accounts share the password documented in README-javafx.md.
INSERT INTO user_data (name, login, phone, password_hash, role)
SELECT demo.name, demo.login, demo.phone,
       '$2a$10$IWoktSWaOUgzR76AozPhS.3PVoZ6ImKK73Q5gxaukmLftRgKTA14W', demo.role
FROM (VALUES
    ('Демо-администратор', 'demo_admin', '+7 900 000-00-01', 'ADMIN'),
    ('Анна Смирнова', 'demo_anna', '+7 900 000-00-02', 'USER'),
    ('Иван Петров', 'demo_ivan', '+7 900 000-00-03', 'USER')
) AS demo(name, login, phone, role)
WHERE NOT EXISTS (SELECT 1 FROM user_data u WHERE u.login = demo.login);

INSERT INTO movie (title, duration, age_rating, description)
SELECT demo.title, demo.duration, demo.age_rating, demo.description
FROM (VALUES
    ('Орбита', 128, '12+', 'Научно-фантастическая история о путешествии к далёкой станции.'),
    ('Тихий город', 104, '16+', 'Детектив о пропаже музейного экспоната.'),
    ('Остров загадок', 96, '6+', 'Семейное приключение на необитаемом острове.'),
    ('Рассвет над морем', 142, '12+', 'Драма о выборе между мечтой и привычной жизнью.'),
    ('Последний экспресс', 112, '18+', 'Напряжённый триллер в ночном поезде.'),
    ('Приключения Лисы', 78, '0+', 'Анимационная сказка для всей семьи.')
) AS demo(title, duration, age_rating, description)
WHERE NOT EXISTS (SELECT 1 FROM movie m WHERE m.title = demo.title);

INSERT INTO hall (number)
SELECT demo.number
FROM (VALUES (1), (2), (3)) AS demo(number)
WHERE NOT EXISTS (SELECT 1 FROM hall h WHERE h.number = demo.number);

INSERT INTO seat (hall_id, row_number, seat_number)
SELECT h.id, r.row_number, n.seat_number
FROM hall h
CROSS JOIN generate_series(1, 5) AS r(row_number)
CROSS JOIN generate_series(1, 8) AS n(seat_number)
WHERE h.number IN (1, 2, 3)
  AND NOT EXISTS (
      SELECT 1 FROM seat s
      WHERE s.hall_id = h.id AND s.row_number = r.row_number AND s.seat_number = n.seat_number
  );

INSERT INTO session (movie_id, hall_id, start_time, price)
SELECT m.id, h.id,
       CURRENT_DATE + demo.day_offset + make_interval(hours => demo.hour, mins => demo.minute),
       demo.price
FROM (VALUES
    ('Орбита', 1, 1, 18, 30, 520),
    ('Тихий город', 2, 1, 19, 0, 480),
    ('Остров загадок', 3, 2, 12, 0, 350),
    ('Рассвет над морем', 1, 2, 16, 30, 560),
    ('Последний экспресс', 2, 3, 14, 0, 600),
    ('Орбита', 3, 3, 19, 30, 550),
    ('Приключения Лисы', 1, 4, 11, 0, 320),
    ('Остров загадок', 2, 5, 13, 0, 380)
) AS demo(title, hall_number, day_offset, hour, minute, price)
JOIN LATERAL (SELECT id FROM movie WHERE title = demo.title ORDER BY id LIMIT 1) m ON TRUE
JOIN hall h ON h.number = demo.hall_number
WHERE NOT EXISTS (
    SELECT 1 FROM session s
    WHERE s.movie_id = m.id AND s.hall_id = h.id
      AND s.start_time = CURRENT_DATE + demo.day_offset + make_interval(hours => demo.hour, mins => demo.minute)
);

INSERT INTO booking (user_id, session_id, seat_id, booking_time)
SELECT u.id, s.id, seat.id, CURRENT_TIMESTAMP
FROM (VALUES
    ('demo_anna', 'Орбита', 1, 1, 1, 1),
    ('demo_ivan', 'Орбита', 1, 1, 1, 2),
    ('demo_anna', 'Тихий город', 2, 1, 2, 4)
) AS demo(login, title, hall_number, day_offset, row_number, seat_number)
JOIN user_data u ON u.login = demo.login
JOIN movie m ON m.title = demo.title
JOIN hall h ON h.number = demo.hall_number
JOIN session s ON s.movie_id = m.id AND s.hall_id = h.id
    AND s.start_time::date = CURRENT_DATE + demo.day_offset
JOIN seat ON seat.hall_id = h.id AND seat.row_number = demo.row_number AND seat.seat_number = demo.seat_number
WHERE NOT EXISTS (SELECT 1 FROM booking b WHERE b.session_id = s.id AND b.seat_id = seat.id);
