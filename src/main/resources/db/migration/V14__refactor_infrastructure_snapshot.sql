-- Drop old infrastructure_snapshot table
DROP TABLE IF EXISTS infrastructure_snapshot CASCADE;

-- Create city_snapshot table
CREATE TABLE city_snapshot
(
    city_id             BIGINT                      NOT NULL,
    city_name           VARCHAR(255)                NOT NULL,

    CONSTRAINT pk_city_snapshot
        PRIMARY KEY (city_id)
);

-- Create venue_snapshot table
CREATE TABLE venue_snapshot
(
    venue_id            BIGINT                      NOT NULL,
    city_id             BIGINT                      NOT NULL,
    venue_name          VARCHAR(255)                NOT NULL,

    CONSTRAINT pk_venue_snapshot
        PRIMARY KEY (venue_id),

    CONSTRAINT fk_venue_snapshot_city
        FOREIGN KEY (city_id)
            REFERENCES city_snapshot (city_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_venue_snapshot_city_id
    ON venue_snapshot (city_id);

-- Create hall_snapshot table
CREATE TABLE hall_snapshot
(
    hall_id             BIGINT                      NOT NULL,
    venue_id            BIGINT                      NOT NULL,
    hall_name           VARCHAR(255)                NOT NULL,

    CONSTRAINT pk_hall_snapshot
        PRIMARY KEY (hall_id),

    CONSTRAINT fk_hall_snapshot_venue
        FOREIGN KEY (venue_id)
            REFERENCES venue_snapshot (venue_id)
            ON DELETE CASCADE
);

-- Add foreign key constraints to event_sessions
ALTER TABLE event_sessions
    ADD CONSTRAINT fk_event_sessions_city
        FOREIGN KEY (city_id)
            REFERENCES city_snapshot (city_id),
    ADD CONSTRAINT fk_event_sessions_venue
        FOREIGN KEY (venue_id)
            REFERENCES venue_snapshot (venue_id),
    ADD CONSTRAINT fk_event_sessions_hall
        FOREIGN KEY (hall_id)
            REFERENCES hall_snapshot (hall_id);
