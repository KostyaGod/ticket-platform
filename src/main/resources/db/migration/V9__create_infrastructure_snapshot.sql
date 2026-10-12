CREATE TABLE infrastructure_snapshot
(
    event_session_id    BIGINT                      NOT NULL,
    city_id             BIGINT                      NOT NULL,
    hall_id             BIGINT                      NOT NULL,
    hall_name           VARCHAR(255)                NOT NULL,
    venue_id            BIGINT                      NOT NULL,
    venue_name          VARCHAR(255)                NOT NULL,
    snapshot_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_infrastructure_snapshot
        PRIMARY KEY (event_session_id),

    CONSTRAINT fk_infrastructure_snapshot_event_session
        FOREIGN KEY (event_session_id)
            REFERENCES event_sessions (id)
);

