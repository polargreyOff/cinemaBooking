--liquibase formatted sql

--changeset kartsev:drop-status-column

ALTER TABLE booking
    DROP COLUMN status;