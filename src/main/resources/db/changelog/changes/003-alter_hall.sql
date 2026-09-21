--liquibase formatted sql

--changeset kartsev:003

ALTER TABLE hall
    DROP COLUMN name;

ALTER TABLE hall
    ADD COLUMN number INTEGER NOT NULL;

ALTER TABLE hall
    ADD CONSTRAINT uq_hall_number UNIQUE (number);