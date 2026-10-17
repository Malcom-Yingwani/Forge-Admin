CREATE TABLE scheduled_publication (
    blog_id uuid PRIMARY KEY REFERENCES blog_post(id) ON DELETE CASCADE,
    scheduled_at timestamptz NOT NULL
);
CREATE INDEX idx_scheduled_publication_due ON scheduled_publication(scheduled_at);
