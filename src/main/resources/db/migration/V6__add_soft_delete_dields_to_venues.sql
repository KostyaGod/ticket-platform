ALTER TABLE venues
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deletion_operation_id UUID;