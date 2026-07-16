CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE;

CREATE TABLE users
(
    id            BIGINT                      NOT NULL DEFAULT nextval('user_seq'),
    username      VARCHAR(80)                 NOT NULL,
    email         VARCHAR(255)                NOT NULL,
    password_hash VARCHAR(255)                NOT NULL,
    role          VARCHAR(50)                 NOT NULL,
    status        VARCHAR(50)                 NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);