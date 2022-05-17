BEGIN;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    price FLOAT NOT NULL,
    type VARCHAR
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(250) UNIQUE NOT NULL,
    username VARCHAR(250) UNIQUE NOT NULL
);

CREATE TABLE payments(
    id SERIAL PRIMARY KEY,
    creditCardNumber INT NOT NULL UNIQUE,
    cvv INT NOT NULL,
    expirationDate DATE NOT NULL,
    userId INT REFERENCES users(id)
);

CREATE TABLE orders(
    id SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    ticketId INT REFERENCES tickets(id),
    userId INT REFERENCES users(id),
    paymentId INT REFERENCES payments(id)
);

INSERT INTO users ( email, username) VALUES ('user@email.it', 'user1');
INSERT INTO payments(creditCardNumber, cvv, expirationDate, userId) VALUES (123456789, 123, '03-08-2024', 1);
INSERT INTO tickets(price, type) VALUES (123, 'normal');
INSERT INTO orders(quantity, ticketId, userId, paymentId) VALUES (2, 1, 1, 1);


COMMIT;