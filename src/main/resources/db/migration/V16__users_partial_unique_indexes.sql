ALTER TABLE users DROP CONSTRAINT IF EXISTS uq_users_username;
ALTER TABLE users DROP CONSTRAINT IF EXISTS uq_users_email;

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_username_active ON users (username) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email_active ON users (email) WHERE deleted_at IS NULL;
