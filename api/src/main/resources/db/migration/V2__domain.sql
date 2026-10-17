-- Domain tables for Forge. Schema changes are managed by Flyway, never Hibernate.
-- Category fields for people and contact messages remain strings by project decision.
-- All foreign keys preserve referenced rows; deleting related data must be explicit.

CREATE TABLE media_asset (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    storage_key text NOT NULL UNIQUE,
    original_name varchar(255) NOT NULL,
    content_type varchar(255) NOT NULL,
    size bigint NOT NULL CHECK (size >= 0),
    sha256 varchar(255) NOT NULL,
    width integer CHECK (width >= 0),
    height integer CHECK (height >= 0),
    duration integer CHECK (duration >= 0),
    variants jsonb
);

CREATE TABLE app_user (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    email varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    display_name varchar(255) NOT NULL,
    role varchar(255) NOT NULL CHECK (role IN ('ADMIN', 'EDITOR')),
    enabled boolean NOT NULL DEFAULT false,
    last_login_at timestamptz
);

CREATE TABLE refresh_token (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    user_id uuid NOT NULL,
    token_hash varchar(255) NOT NULL UNIQUE,
    expires_at timestamptz NOT NULL,
    revoked boolean NOT NULL DEFAULT false
);

CREATE TABLE preacher (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    title varchar(255),
    bio text,
    photo_id uuid
);

CREATE TABLE sermon_series (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    title varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    description text,
    cover_image_id uuid,
    start_date date,
    end_date date,
    CONSTRAINT ck_series_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE TABLE sermon (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    title varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    preached_on date NOT NULL,
    service varchar(255) NOT NULL CHECK (service IN ('MORNING', 'EVENING', 'BIBLE_HOUR', 'OTHER')),
    preacher_id uuid NOT NULL,
    series_id uuid,
    scripture_book varchar(255),
    scripture_ref varchar(255),
    summary text,
    audio_id uuid,
    video_url text,
    duration_seconds integer CHECK (duration_seconds >= 0),
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE ministry (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    description text,
    meeting_info text,
    contact text,
    contact_public boolean NOT NULL DEFAULT false,
    image_id uuid,
    sort_order integer NOT NULL DEFAULT 0,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE event (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    title varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    starts_at timestamptz NOT NULL,
    ends_at timestamptz,
    all_day boolean NOT NULL DEFAULT false,
    location text,
    description text,
    cover_id uuid,
    recurrence varchar(255) NOT NULL CHECK (recurrence IN ('NONE', 'WEEKLY', 'MONTHLY')),
    recurrence_until date,
    ministry_id uuid,
    published boolean NOT NULL DEFAULT false,
    CONSTRAINT ck_event_dates CHECK (ends_at IS NULL OR ends_at >= starts_at)
);

CREATE TABLE page (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    slug varchar(255) NOT NULL UNIQUE,
    title varchar(255) NOT NULL,
    body text NOT NULL,
    section varchar(255) NOT NULL CHECK (section IN ('ABOUT', 'RESOURCES', 'CONTACT', 'OTHER')),
    sort_order integer NOT NULL DEFAULT 0,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE person (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    name varchar(255) NOT NULL,
    category varchar(255) NOT NULL CHECK (category IN ('LEADERSHIP', 'OFFICE_STAFF')),
    office varchar(255) CHECK (office IN ('ELDER', 'DEACON')),
    role_title varchar(255),
    bio text,
    photo_id uuid,
    public_email varchar(255),
    consent_at timestamptz,
    consent_by_id uuid,
    sort_order integer NOT NULL DEFAULT 0,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE blog_post (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    title varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    author_id uuid,
    excerpt text,
    body text NOT NULL,
    cover_image_id uuid,
    tags jsonb,
    published_at timestamptz,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE document (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    title varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    category varchar(255) NOT NULL CHECK (category IN ('CREED', 'CONFESSION', 'CATECHISM', 'OTHER')),
    year integer,
    origin varchar(255),
    summary text,
    body text,
    pdf_id uuid,
    sort_order integer NOT NULL DEFAULT 0,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE giving_fund (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL UNIQUE,
    description text,
    account_name varchar(255) NOT NULL,
    bank varchar(255) NOT NULL,
    branch_code varchar(255) NOT NULL,
    account_number varchar(255) NOT NULL,
    account_type varchar(255) NOT NULL,
    payment_reference varchar(255),
    snapscan_url text,
    snapscan_qr_id uuid,
    target_amount numeric(12,2),
    raised_amount numeric(12,2),
    show_progress boolean NOT NULL DEFAULT false,
    sort_order integer NOT NULL DEFAULT 0,
    published boolean NOT NULL DEFAULT false
);

CREATE TABLE growth_group (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    name varchar(255) NOT NULL,
    area varchar(255),
    host varchar(255),
    day_of_week varchar(255) NOT NULL CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    time time(6) NOT NULL,
    contact text,
    contact_public boolean NOT NULL DEFAULT false,
    active boolean NOT NULL DEFAULT false
);

CREATE TABLE service_time (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    name varchar(255) NOT NULL,
    day_of_week varchar(255) NOT NULL CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    time time(6) NOT NULL,
    notes text,
    active boolean NOT NULL DEFAULT false
);

CREATE TABLE site_settings (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    church_name varchar(255) NOT NULL,
    address text NOT NULL,
    latitude numeric(10,7),
    longitude numeric(10,7),
    email varchar(255),
    phone varchar(255),
    socials jsonb,
    hero_headline varchar(255),
    hero_image_id uuid,
    hero_cta_text varchar(255),
    hero_cta_url text,
    footer_text text,
    CONSTRAINT ck_site_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_site_longitude CHECK (longitude BETWEEN -180 AND 180)
);

CREATE TABLE contact_message (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    category varchar(255) NOT NULL CHECK (category IN ('GENERAL', 'LIFT_REQUEST')),
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    phone varchar(255),
    subject varchar(255),
    message text NOT NULL,
    area varchar(255),
    service varchar(255) CHECK (service IN ('MORNING', 'EVENING', 'BIBLE_HOUR', 'OTHER')),
    received_at timestamptz NOT NULL,
    handled boolean NOT NULL DEFAULT false,
    handled_by_id uuid
);

CREATE TABLE issue_report (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    reporter_id uuid NOT NULL,
    category varchar(255) NOT NULL CHECK (category IN ('BUG', 'CONTENT', 'DATA', 'OTHER')),
    description text NOT NULL,
    page_url text,
    user_agent text,
    status varchar(255) NOT NULL
);

CREATE TABLE audit_log (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    actor_id uuid,
    action varchar(255) NOT NULL,
    entity_type varchar(255) NOT NULL,
    entity_id uuid NOT NULL,
    at timestamptz NOT NULL,
    diff jsonb
);

CREATE TABLE sync_outbox (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    event_id uuid NOT NULL UNIQUE,
    kind varchar(255) NOT NULL CHECK (kind IN ('EVENT', 'MEDIA', 'SNAPSHOT')),
    type varchar(255) NOT NULL,
    entity_id uuid NOT NULL,
    payload jsonb NOT NULL,
    attempts integer NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    next_attempt_at timestamptz NOT NULL,
    last_error text,
    status varchar(255) NOT NULL CHECK (status IN ('PENDING', 'SENT', 'DEAD'))
);

CREATE TABLE faq (
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    version bigint NOT NULL DEFAULT 0,
    question varchar(255) NOT NULL,
    answer text NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    published boolean NOT NULL DEFAULT false
);

-- Relationships are added after all tables exist.
ALTER TABLE refresh_token ADD CONSTRAINT fk_refresh_token_user_id FOREIGN KEY (user_id) REFERENCES app_user (id);
ALTER TABLE preacher ADD CONSTRAINT fk_preacher_photo_id FOREIGN KEY (photo_id) REFERENCES media_asset (id);
ALTER TABLE sermon_series ADD CONSTRAINT fk_sermon_series_cover_image_id FOREIGN KEY (cover_image_id) REFERENCES media_asset (id);
ALTER TABLE sermon ADD CONSTRAINT fk_sermon_preacher_id FOREIGN KEY (preacher_id) REFERENCES preacher (id);
ALTER TABLE sermon ADD CONSTRAINT fk_sermon_series_id FOREIGN KEY (series_id) REFERENCES sermon_series (id);
ALTER TABLE sermon ADD CONSTRAINT fk_sermon_audio_id FOREIGN KEY (audio_id) REFERENCES media_asset (id);
ALTER TABLE ministry ADD CONSTRAINT fk_ministry_image_id FOREIGN KEY (image_id) REFERENCES media_asset (id);
ALTER TABLE event ADD CONSTRAINT fk_event_cover_id FOREIGN KEY (cover_id) REFERENCES media_asset (id);
ALTER TABLE event ADD CONSTRAINT fk_event_ministry_id FOREIGN KEY (ministry_id) REFERENCES ministry (id);
ALTER TABLE person ADD CONSTRAINT fk_person_photo_id FOREIGN KEY (photo_id) REFERENCES media_asset (id);
ALTER TABLE person ADD CONSTRAINT fk_person_consent_by_id FOREIGN KEY (consent_by_id) REFERENCES app_user (id);
ALTER TABLE blog_post ADD CONSTRAINT fk_blog_post_author_id FOREIGN KEY (author_id) REFERENCES app_user (id);
ALTER TABLE blog_post ADD CONSTRAINT fk_blog_post_cover_image_id FOREIGN KEY (cover_image_id) REFERENCES media_asset (id);
ALTER TABLE document ADD CONSTRAINT fk_document_pdf_id FOREIGN KEY (pdf_id) REFERENCES media_asset (id);
ALTER TABLE giving_fund ADD CONSTRAINT fk_giving_fund_snapscan_qr_id FOREIGN KEY (snapscan_qr_id) REFERENCES media_asset (id);
ALTER TABLE site_settings ADD CONSTRAINT fk_site_settings_hero_image_id FOREIGN KEY (hero_image_id) REFERENCES media_asset (id);
ALTER TABLE contact_message ADD CONSTRAINT fk_contact_message_handled_by_id FOREIGN KEY (handled_by_id) REFERENCES app_user (id);
ALTER TABLE issue_report ADD CONSTRAINT fk_issue_report_reporter_id FOREIGN KEY (reporter_id) REFERENCES app_user (id);
ALTER TABLE audit_log ADD CONSTRAINT fk_audit_log_actor_id FOREIGN KEY (actor_id) REFERENCES app_user (id);

CREATE INDEX idx_sermon_preached_on ON sermon (preached_on DESC);
CREATE INDEX idx_sermon_series_id ON sermon (series_id);
CREATE INDEX idx_sermon_preacher_id ON sermon (preacher_id);
CREATE INDEX idx_sermon_scripture_book ON sermon (scripture_book);
CREATE INDEX idx_sermon_service ON sermon (service);
CREATE INDEX idx_blog_post_published_at ON blog_post (published_at DESC);
CREATE INDEX idx_sync_outbox_delivery ON sync_outbox (status, next_attempt_at);
CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
CREATE INDEX idx_contact_message_handled_by ON contact_message (handled_by_id);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);

-- A unique constant expression permits at most one SiteSettings row.
CREATE UNIQUE INDEX uq_site_settings_singleton ON site_settings ((true));
