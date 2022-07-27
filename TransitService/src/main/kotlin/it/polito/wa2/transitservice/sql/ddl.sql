BEGIN;

DROP TABLE IF EXISTS transits CASCADE;

CREATE TABLE transits(
    id          SERIAL PRIMARY KEY,
    ticketID    VARCHAR(50) NOT NULL,
    userID      INT         NOT NULL,
    turnstileID INT         NOT NULL,
    time        TIMESTAMP   NOT NULL
);

COMMIT;