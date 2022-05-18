BEGIN;

DROP TABLE IF EXISTS payments CASCADE;

CREATE TABLE payments(
    paymentid INT PRIMARY KEY NOT NULL,
    userid INT NOT NULL,
    status INT
);

COMMIT;
