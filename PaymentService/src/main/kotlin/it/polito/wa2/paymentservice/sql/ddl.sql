BEGIN;

DROP TABLE IF EXISTS payment CASCADE;

CREATE TABLE payment(
    paymentid SERIAL PRIMARY KEY,
    orderid INT NOT NULL,
    userid INT NOT NULL,
    status INT,
    time TIMESTAMP NOT NULL
);

COMMIT;