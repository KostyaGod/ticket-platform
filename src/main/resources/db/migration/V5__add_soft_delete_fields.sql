ALTER TABLE cities
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deletion_operation_id UUID;

ALTER TABLE halls
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deletion_operation_id UUID;

ALTER TABLE seats
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deletion_operation_id UUID;

ALTER TABLE events
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deletion_operation_id UUID;

ALTER TABLE event_sessions
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deletion_operation_id UUID;