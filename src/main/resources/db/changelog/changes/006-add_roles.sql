--liquibase formatted sql

--changeset kartsev:add-roles

ALTER TABLE user_data
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

ALTER TABLE user_data
    ADD CONSTRAINT chk_user_role
        CHECK (role IN ('USER', 'ADMIN'));