BEGIN;

DROP TABLE IF EXISTS Administrator CASCADE;

CREATE TABLE Administrator(
    id SERIAL PRIMARY KEY,
    username VARCHAR,
    password VARCHAR,
    email VARCHAR,
    enroll INT NOT NULL,
    salt VARCHAR,
    role VARCHAR,
    enabled INT NOT NULL
);

INSERT INTO Administrator (id, username, password, email, enroll, salt, role, enabled) VALUES (1, 'admin', 'admin', NULL, 1, '', 'ADMIN', 1);

COMMIT;