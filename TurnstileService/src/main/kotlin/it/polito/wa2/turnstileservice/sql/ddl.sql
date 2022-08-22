BEGIN;

DROP TABLE IF EXISTS turnstiles CASCADE;

CREATE TABLE turnstiles(
    id       SERIAL PRIMARY KEY,
    username VARCHAR,
    password VARCHAR,
    salt     VARCHAR
);


INSERT INTO turnstiles (id, username, password, salt)
VALUES (DEFAULT,
        'turnstile1',
        '$2a$10$H7gLDlbxWWfzXstNxSbdUeM2ol463tH4g6H9tv9cd/CHUTpPA4D5m',
        '$2a$10$H7gLDlbxWWfzXstNxSbdUe');

COMMIT;