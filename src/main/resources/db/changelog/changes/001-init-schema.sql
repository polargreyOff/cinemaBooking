--liquibase formatted sql

--changeset kartsev:001

CREATE TABLE movie (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       duration INTEGER NOT NULL,
                       age_rating VARCHAR(10)
);

CREATE TABLE hall (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL
);

CREATE TABLE seat (
                      id BIGSERIAL PRIMARY KEY,
                      hall_id BIGINT NOT NULL REFERENCES hall(id),
                      row_number INTEGER NOT NULL,
                      seat_number INTEGER NOT NULL,

                      UNIQUE (hall_id, row_number, seat_number)
);

CREATE TABLE user_data (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           phone VARCHAR(30)
);

CREATE TABLE session (
                         id BIGSERIAL PRIMARY KEY,
                         movie_id BIGINT NOT NULL REFERENCES movie(id),
                         hall_id BIGINT NOT NULL REFERENCES hall(id),
                         start_time TIMESTAMP NOT NULL,
                         price INTEGER NOT NULL
);

CREATE TABLE booking (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES user_data(id),
                         session_id BIGINT NOT NULL REFERENCES session(id),
                         seat_id BIGINT NOT NULL REFERENCES seat(id),
                         booking_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',

                         UNIQUE (session_id, seat_id)
);