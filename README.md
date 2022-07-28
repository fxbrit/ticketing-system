# Ticketing System

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
```

To create the table run `SQL/ddl.sql`

Verify table creation with
```
psql -h localhost -p 5432 -U postgres payments 
\d payments
```
and exit with `\q`.

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
createdb -h localhost -p 5432 -U postgres orders
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