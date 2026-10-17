CREATE UNIQUE INDEX uq_app_user_email_lower ON app_user (lower(email));
CREATE TABLE password_reset_token (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    user_id uuid NOT NULL REFERENCES app_user(id),
    token_hash varchar(255) NOT NULL UNIQUE,
    expires_at timestamptz NOT NULL,
    used boolean NOT NULL DEFAULT false
);
CREATE INDEX idx_password_reset_user ON password_reset_token(user_id);
