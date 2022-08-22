BEGIN;

DROP TABLE IF EXISTS application_user CASCADE;

CREATE TABLE application_user(
    id SERIAL PRIMARY KEY,
    username VARCHAR,
    password VARCHAR,
    email VARCHAR,
    salt VARCHAR,
    role VARCHAR,
    enabled INT NOT NULL
);

INSERT INTO application_user (id, username, password, email, salt, role, enabled) VALUES (DEFAULT, 'admin', '$2a$10$S3gf6X9T9O35Q5StYhg30ufWAfZ8OBH3cHmCNMMbpUjf/Z6cnWqua', 'superadmin@admin_mail.com', '$2a$10$S3gf6X9T9O35Q5StYhg30u', '2', 1);

COMMIT;