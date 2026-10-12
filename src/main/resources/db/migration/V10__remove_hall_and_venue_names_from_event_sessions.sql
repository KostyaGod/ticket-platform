ALTER TABLE event_sessions
    DROP COLUMN IF EXISTS venue_name,
    DROP COLUMN IF EXISTS hall_name;