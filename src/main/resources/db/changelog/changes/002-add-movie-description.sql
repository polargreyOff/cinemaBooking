--liquibase formatted sql

--changeset kartsev:002

ALTER TABLE movie
    ADD COLUMN description TEXT;