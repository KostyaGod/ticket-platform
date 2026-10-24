ALTER TABLE events
    ADD CONSTRAINT age_limit_check CHECK (age_limit >= 0 AND age_limit <= 150);
