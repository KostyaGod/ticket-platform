CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Functional GIN trigram indexes matching cb.lower(...) in JPA Specifications:
CREATE INDEX IF NOT EXISTS idx_events_lower_name_trgm ON events USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_venues_lower_name_trgm ON venues USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_cities_lower_name_trgm ON cities USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_halls_lower_name_trgm ON halls USING gin (lower(name) gin_trgm_ops);
