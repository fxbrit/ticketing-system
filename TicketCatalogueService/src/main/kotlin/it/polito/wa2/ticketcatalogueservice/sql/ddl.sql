BEGIN;

DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;

CREATE TABLE tickets(
    id        SERIAL PRIMARY KEY,
    price     FLOAT NOT NULL,
    type      VARCHAR(50) NOT NULL,
    reduction VARCHAR(50),
    max_age   INT,
    min_age   INT
);

CREATE TABLE orders(
    id       SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    ticketId INT REFERENCES tickets (id),
    userId   INT,
    time     TIMESTAMP NOT NULL,
    status   VARCHAR(255) NOT NULL
);

INSERT INTO tickets (price, type, reduction, max_age, min_age) VALUES (2, 'one way', NULL, NULL, NULL);
INSERT INTO tickets (price, type, reduction, max_age, min_age) VALUES (5.50, 'weekend', NULL, NULL, NULL);
INSERT INTO tickets (price, type, reduction, max_age, min_age) VALUES (40, 'monthly', NULL, NULL, NULL);
INSERT INTO tickets (price, type, reduction, max_age, min_age) VALUES (25, 'monthly', 'students', 25, NULL);
INSERT INTO tickets (price, type, reduction, max_age, min_age) VALUES (25, 'monthly', 'elders', NULL, 65);

COMMIT;