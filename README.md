# group03-reactive-kafka

## Start Postgres and Kafka
From the root directory of PaymentService run:
```
docker-compose up
```

## Setting up Postgres

### PaymentService

Create the database:
```
createdb -h localhost -p 5432 -U postgres payments
psql -h localhost -p 5432 -U postgres payments 
```

Finally, to create the table:
```postgres-sql
CREATE TABLE payment(
    paymentid SERIAL PRIMARY KEY,
    orderid INT NOT NULL,
    userid INT NOT NULL,
    status INT
);
```

Verify table creation with `\d payments` and exit with `\q`.

### LoginService

Create the database:
```
createdb -h localhost -p 5432 -U postgres registration
```

### TravelerService

Create the database:
```
createdb -h localhost -p 5432 -U postgres tickets
```

### TicketCatalogueService

Create the database:
```
createdb -h localhost -p 5432 -U postgres ordersdb
```

Then run `ddl.sql`

## Services port

| Service name           | Port |
|------------------------|------|
| TicketCatalogueService | 8080 |
| LoginService           | 8081 |
| TravelerService        | 8082 |
| PaymentService         | 8083 |
| BankServiceMock        | 8084 |