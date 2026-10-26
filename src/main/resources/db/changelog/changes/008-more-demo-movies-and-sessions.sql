--liquibase formatted sql

--changeset codex:008-more-demo-movies-and-sessions
-- Keep the original demo bookings while moving their sessions to 30 October 2026 or later.
WITH seed_sessions AS (
    SELECT s.id, s.start_time
    FROM session s
    JOIN movie m ON m.id = s.movie_id
    WHERE m.title IN (
        'Орбита', 'Тихий город', 'Остров загадок',
        'Рассвет над морем', 'Последний экспресс', 'Приключения Лисы'
    )
), shift AS (
    SELECT GREATEST(0, GREATEST(DATE '2026-10-30', CURRENT_DATE + 1) - MIN(start_time::date)) AS days
    FROM seed_sessions
)
UPDATE session s
SET start_time = s.start_time + shift.days * INTERVAL '1 day'
FROM seed_sessions demo CROSS JOIN shift
WHERE s.id = demo.id AND shift.days > 0;

INSERT INTO movie (title, duration, age_rating, description)
SELECT demo.title, demo.duration, demo.age_rating, demo.description
FROM (VALUES
    ('Интерстеллар', 169, '16+', 'Космическое путешествие в поисках нового дома для человечества.'),
    ('Начало', 148, '12+', 'Команда пытается внедрить идею через многоуровневые сны.'),
    ('Тёмный рыцарь', 152, '16+', 'Бэтмен противостоит преступнику, который погружает Готэм в хаос.'),
    ('Аватар', 162, '12+', 'История встречи людей с обитателями далёкой планеты Пандора.'),
    ('Титаник', 195, '12+', 'Любовная история на борту океанского лайнера.'),
    ('Матрица', 136, '16+', 'Программист узнаёт, что привычный мир скрывает другую реальность.'),
    ('Колобок', 70, '0+', 'Семейная сказка о путешествии Колобка и встречах с лесными жителями.')
) AS demo(title, duration, age_rating, description)
WHERE NOT EXISTS (SELECT 1 FROM movie m WHERE m.title = demo.title);

-- Showtimes cover morning, afternoon and evening for the JavaFX time filters.
INSERT INTO session (movie_id, hall_id, start_time, price)
SELECT m.id, h.id,
       GREATEST(DATE '2026-10-30', CURRENT_DATE + 1) + demo.day_offset
           + make_interval(hours => demo.hour, mins => demo.minute),
       demo.price
FROM (VALUES
    ('Колобок', 3, 0, 10, 30, 300),
    ('Начало', 2, 1, 14, 0, 520),
    ('Матрица', 1, 2, 12, 0, 490),
    ('Аватар', 3, 3, 15, 0, 570),
    ('Интерстеллар', 2, 4, 18, 0, 610),
    ('Тёмный рыцарь', 1, 5, 19, 0, 590),
    ('Колобок', 2, 5, 10, 0, 320),
    ('Титаник', 3, 6, 17, 0, 550),
    ('Начало', 1, 6, 13, 0, 530),
    ('Интерстеллар', 1, 7, 18, 0, 620),
    ('Матрица', 2, 7, 20, 0, 510)
) AS demo(title, hall_number, day_offset, hour, minute, price)
JOIN LATERAL (SELECT id FROM movie WHERE title = demo.title ORDER BY id LIMIT 1) m ON TRUE
JOIN hall h ON h.number = demo.hall_number
WHERE NOT EXISTS (
    SELECT 1 FROM session s
    WHERE s.movie_id = m.id AND s.hall_id = h.id
      AND s.start_time = GREATEST(DATE '2026-10-30', CURRENT_DATE + 1) + demo.day_offset
          + make_interval(hours => demo.hour, mins => demo.minute)
);
