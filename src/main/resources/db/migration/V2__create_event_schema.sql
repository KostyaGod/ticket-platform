CREATE SEQUENCE city_seq;
CREATE SEQUENCE venue_seq;
CREATE SEQUENCE hall_seq;
CREATE SEQUENCE seat_seq;
CREATE SEQUENCE event_seq;
CREATE SEQUENCE event_session_seq;


CREATE TABLE cities
(
    id         BIGINT                      NOT NULL DEFAULT nextval('city_seq'),
    name       VARCHAR(255)                NOT NULL,

    CONSTRAINT pk_cities PRIMARY KEY (id)
);

CREATE TABLE venues
(
    id         BIGINT                      NOT NULL DEFAULT nextval('venue_seq'),
    city_id    BIGINT                      NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_venues PRIMARY KEY (id),
    CONSTRAINT fk_venues_city
        FOREIGN KEY (city_id)
            REFERENCES cities (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_venues_city_name
        UNIQUE (city_id, name)
);

CREATE INDEX idx_venues_city_id
    ON venues (city_id);


CREATE TABLE halls
(
    id         BIGINT                      NOT NULL DEFAULT nextval('hall_seq'),
    venue_id   BIGINT                      NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_halls PRIMARY KEY (id),
    CONSTRAINT fk_halls_venue
        FOREIGN KEY (venue_id)
            REFERENCES venues (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_halls_venue_name
        UNIQUE (venue_id, name)
);

CREATE INDEX idx_halls_venue_id
    ON halls (venue_id);


CREATE TABLE seats
(
    id          BIGINT NOT NULL DEFAULT nextval('seat_seq'),
    hall_id     BIGINT NOT NULL,
    row_number  INTEGER NOT NULL,
    seat_number INTEGER NOT NULL,

    CONSTRAINT pk_seats PRIMARY KEY (id),
    CONSTRAINT fk_seats_hall
        FOREIGN KEY (hall_id)
            REFERENCES halls (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_seats_hall_row_number
        UNIQUE (hall_id, row_number, seat_number)
);

CREATE INDEX idx_seats_hall_id
    ON seats (hall_id);


CREATE TABLE events
(
    id          BIGINT                      NOT NULL DEFAULT nextval('event_seq'),
    title       VARCHAR(255)                NOT NULL,
    description TEXT,
    duration    INTEGER,
    category    VARCHAR(100),
    age_limit   INTEGER,
    organizer   VARCHAR(255),
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_events PRIMARY KEY (id)
);


CREATE TABLE event_sessions
(
    id             BIGINT                      NOT NULL DEFAULT nextval('event_session_seq'),
    event_id       BIGINT                      NOT NULL,
    hall_id        BIGINT                      NOT NULL,
    status         VARCHAR(50)                 NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sales_start_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    sales_end_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    start_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_event_sessions PRIMARY KEY (id),

    CONSTRAINT fk_event_sessions_event
        FOREIGN KEY (event_id)
            REFERENCES events (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_event_sessions_hall
        FOREIGN KEY (hall_id)
            REFERENCES halls (id)
            ON DELETE RESTRICT,

    CONSTRAINT chk_event_sessions_sales_period
        CHECK (sales_start_at < sales_end_at),

    CONSTRAINT chk_event_sessions_period
        CHECK (start_at < end_at)
);

CREATE INDEX idx_event_sessions_event_id
    ON event_sessions (event_id);

CREATE INDEX idx_event_sessions_hall_id
    ON event_sessions (hall_id);

CREATE INDEX idx_event_sessions_start_at
    ON event_sessions (start_at);