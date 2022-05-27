BEGIN;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    price FLOAT NOT NULL,
    type VARCHAR,
    max_age INT,
    min_age INT
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(250) UNIQUE NOT NULL,
    username VARCHAR(250) UNIQUE NOT NULL
);

CREATE TABLE orders(
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    ticketId INT REFERENCES tickets(id),
    userId INT REFERENCES users(id),
    status VARCHAR(255) NOT NULL
);

INSERT INTO users (email, username) VALUES ('user@email.it', 'user1');
INSERT INTO tickets (price, type, max_age, min_age) VALUES (123, 'students', 25, NULL);
INSERT INTO tickets (price, type, max_age, min_age) VALUES (123, 'elders', NULL, 65);


COMMIT;