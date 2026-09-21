--liquibase formatted sql

--changeset kartsev:add-user-credentials

ALTER TABLE user_data
    ADD COLUMN login VARCHAR(100) NOT NULL UNIQUE,
    ADD COLUMN password_hash VARCHAR(255) NOT NULL;