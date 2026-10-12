ALTER TABLE event_sessions
    ADD COLUMN venue_name VARCHAR(255),
    ADD COLUMN hall_name VARCHAR(255);

UPDATE event_sessions es
SET
    venue_name = v.name,
    hall_name = h.name
FROM venues v, halls h
WHERE es.venue_id = v.id
  AND es.hall_id = h.id;

ALTER TABLE event_sessions
    ALTER COLUMN venue_name SET NOT NULL,
    ALTER COLUMN hall_name SET NOT NULL;