BEGIN;

DROP TABLE IF EXISTS user_details CASCADE;
DROP TABLE IF EXISTS ticket_purchased CASCADE;

create table user_details(
    user_id          INTEGER NOT NULL PRIMARY KEY,
    role             VARCHAR,
    name             VARCHAR,
    address          VARCHAR,
    date_of_birth    VARCHAR,
    telephone_number VARCHAR
);

create table ticket_purchased(
    ticket_id            UUID NOT NULL PRIMARY KEY,
    ticket_owner_user_id INTEGER REFERENCES user_details(user_id),
    issued_at            TIMESTAMP NOT NULL,
    start_validity       TIMESTAMP,
    end_validity         TIMESTAMP,
    zone_id              VARCHAR
);

INSERT INTO user_details (user_id, role, name, address, date_of_birth, telephone_number)
VALUES (1, '[SUPERADMIN]', 'Paolo Neri', 'Via Ambrosia 6', '1993-01-13', '3204455201');

COMMIT;