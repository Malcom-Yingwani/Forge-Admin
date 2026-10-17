CREATE TABLE media_cleanup (
    storage_key text PRIMARY KEY,
    attempts integer NOT NULL DEFAULT 0,
    next_attempt_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_media_cleanup_due ON media_cleanup(next_attempt_at);
