ALTER TABLE event_sessions
    RENAME COLUMN status TO event_status;

ALTER TABLE event_sessions
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users
    RENAME COLUMN status TO user_status;

ALTER TABLE users
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';