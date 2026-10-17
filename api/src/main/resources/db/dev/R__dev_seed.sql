-- Development examples only. No accounts, credentials, real personal data or bank details.
-- Stable IDs and conflict handling allow changed repeatable migrations to run safely
-- without overwriting content that a developer has edited.
-- The address below is the church's documented public address.

INSERT INTO service_time (id, created_at, updated_at, version, name, day_of_week, time, notes, active) VALUES
    ('00000000-0000-4000-8000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Morning Service', 'SUNDAY', '09:30:00', 'Development example', true),
    ('00000000-0000-4000-8000-000000000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Evening Service', 'SUNDAY', '18:00:00', 'Development example', true)
ON CONFLICT DO NOTHING;

INSERT INTO ministry (id, created_at, updated_at, version, name, slug, description, sort_order, published) VALUES
    ('00000000-0000-4000-8000-000000000010', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Growth Groups', 'growth-groups', 'Development example: small groups meeting during the week.', 0, true),
    ('00000000-0000-4000-8000-000000000011', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Young Adults', 'young-adults', 'Development example: young adults ministry.', 1, true),
    ('00000000-0000-4000-8000-000000000012', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Women''s Ministry', 'womens-ministry', 'Development example: women''s ministry.', 2, true),
    ('00000000-0000-4000-8000-000000000013', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Men''s Ministry', 'mens-ministry', 'Development example: men''s ministry.', 3, true)
ON CONFLICT DO NOTHING;

INSERT INTO giving_fund (id, created_at, updated_at, version, name, slug, description, account_name, bank, branch_code, account_number, account_type, payment_reference, target_amount, raised_amount, show_progress, sort_order, published) VALUES
    ('00000000-0000-4000-8000-000000000020', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'General Fund', 'general', 'Development example only. Do not make payments.', 'DUMMY ACCOUNT', 'DUMMY BANK', '000000', '0000000000', 'DUMMY', 'DEMO-GENERAL', NULL, NULL, false, 0, true),
    ('00000000-0000-4000-8000-000000000021', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'New Church Building Fund', 'new-church-building-fund', 'Development example only. Do not make payments.', 'DUMMY ACCOUNT', 'DUMMY BANK', '000000', '0000000000', 'DUMMY', 'DEMO-BUILDING', 100000, 12500, true, 1, true),
    ('00000000-0000-4000-8000-000000000022', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Church Planting Fund', 'church-planting-fund', 'Development example only. Do not make payments.', 'DUMMY ACCOUNT', 'DUMMY BANK', '000000', '0000000000', 'DUMMY', 'DEMO-PLANTING', 50000, 5000, true, 2, true)
ON CONFLICT DO NOTHING;

INSERT INTO preacher (id, created_at, updated_at, version, name, slug, title, bio) VALUES
    ('00000000-0000-4000-8000-000000000030', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Example Preacher One', 'example-preacher-one', 'Guest', 'Fictional development example.'),
    ('00000000-0000-4000-8000-000000000031', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Example Preacher Two', 'example-preacher-two', 'Elder', 'Fictional development example.')
ON CONFLICT DO NOTHING;

INSERT INTO sermon_series (id, created_at, updated_at, version, title, slug, description, start_date, end_date) VALUES
    ('00000000-0000-4000-8000-000000000040', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Beginnings: Sample Series', 'beginnings-sample-series', 'Development examples for testing sermon listings.', '2026-10-04', '2026-10-18')
ON CONFLICT DO NOTHING;

INSERT INTO sermon (id, created_at, updated_at, version, title, slug, preached_on, service, preacher_id, series_id, scripture_book, scripture_ref, summary, published) VALUES
    ('00000000-0000-4000-8000-000000000050', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'In the Beginning: Sample Sermon', 'in-the-beginning-sample', '2026-10-04', 'MORNING', '00000000-0000-4000-8000-000000000030', '00000000-0000-4000-8000-000000000040', 'GENESIS', 'Genesis 1:1-5', 'Development example. Audio has not been uploaded.', true),
    ('00000000-0000-4000-8000-000000000051', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'A Good Creation: Sample Sermon', 'a-good-creation-sample', '2026-10-11', 'EVENING', '00000000-0000-4000-8000-000000000031', '00000000-0000-4000-8000-000000000040', 'GENESIS', 'Genesis 1:26-31', 'Development example. Audio has not been uploaded.', true),
    ('00000000-0000-4000-8000-000000000052', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Rest: Sample Sermon', 'rest-sample', '2026-10-18', 'BIBLE_HOUR', '00000000-0000-4000-8000-000000000030', '00000000-0000-4000-8000-000000000040', 'GENESIS', 'Genesis 2:1-3', 'Development example. Audio has not been uploaded.', true)
ON CONFLICT DO NOTHING;

INSERT INTO blog_post (id, created_at, updated_at, version, title, slug, excerpt, body, tags, published_at, published) VALUES
    ('00000000-0000-4000-8000-000000000060', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Welcome: Sample Post', 'welcome-sample-post', 'A development example.', '# Welcome

This is fictional development content.', '["news"]', '2026-10-01T08:00:00Z', true),
    ('00000000-0000-4000-8000-000000000061', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'This Week: Sample Post', 'this-week-sample-post', 'Another development example.', '# This week

Replace this sample with approved church content.', '["church","news"]', '2026-10-08T08:00:00Z', true)
ON CONFLICT DO NOTHING;

INSERT INTO faq (id, created_at, updated_at, version, question, answer, sort_order, published) VALUES
    ('00000000-0000-4000-8000-000000000070', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'When are Sunday services?', 'Development example: morning at 09:30 and evening at 18:00.', 0, true),
    ('00000000-0000-4000-8000-000000000071', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'What should I expect on my first visit?', 'Development example: replace this answer with approved visitor information.', 1, true),
    ('00000000-0000-4000-8000-000000000072', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'How can I learn about growth groups?', 'Development example: see the Growth Groups ministry page.', 2, true)
ON CONFLICT DO NOTHING;

INSERT INTO page (id, created_at, updated_at, version, title, slug, body, section, sort_order, published) VALUES
    ('00000000-0000-4000-8000-000000000080', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'A Brief History', 'a-brief-history', '# A Brief History

Development placeholder; this is not an account of the church''s history.', 'ABOUT', 0, true),
    ('00000000-0000-4000-8000-000000000081', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, '1689 Baptist Confession', '1689-baptist-confession', '# 1689 Baptist Confession

Development placeholders only; the following is not the text of the confession.

## Chapter 1: Of the Holy Scriptures

Sample chapter content for testing Markdown rendering.

## Chapter 2: Of God and of the Holy Trinity

Sample chapter content for testing a second chapter.', 'RESOURCES', 1, true),
    ('00000000-0000-4000-8000-000000000082', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Potchefstroom Church Plant', 'potchefstroom-church-plant', '# Potchefstroom Church Plant

Development placeholder; replace with approved information.', 'ABOUT', 2, true),
    ('00000000-0000-4000-8000-000000000083', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Bible Hour', 'bible-hour', '# Bible Hour

Development placeholder for Bible Hour information.', 'RESOURCES', 3, true),
    ('00000000-0000-4000-8000-000000000084', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Do You Need a Lift?', 'do-you-need-a-lift', '# Do you need a lift?

Development placeholder for lift request information.', 'CONTACT', 4, true),
    ('00000000-0000-4000-8000-000000000085', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Privacy Notice', 'privacy-notice', '# Privacy notice

Development placeholder; this is not a production privacy notice.', 'OTHER', 5, true)
ON CONFLICT DO NOTHING;

INSERT INTO site_settings (id, created_at, updated_at, version, church_name, address, hero_headline, hero_cta_text, hero_cta_url, footer_text) VALUES
    ('00000000-0000-4000-8000-000000000090', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Heritage Baptist Church', '41 7th Avenue, Melville, Johannesburg', 'Development example: welcome to Heritage', 'Plan a visit', '/contact', 'Development example only.')
ON CONFLICT DO NOTHING;
