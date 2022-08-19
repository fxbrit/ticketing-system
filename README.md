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

To create the table run `sql/ddl.sql`

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

Then run `sql/ddl.sql`

### TravelerService

Create the database:

```
createdb -h localhost -p 5432 -U postgres tickets
```

Then run `sql/ddl.sql`

### TicketCatalogueService

Create the database:

```
createdb -h localhost -p 5432 -U postgres orders
```

Then run `sql/ddl.sql`

### TransitService

```
createdb -h localhost -p 5432 -U postgres transits
```

## Services port

| Service name           | Port |
|------------------------|------|
| TicketCatalogueService | 8080 |
| LoginService           | 8081 |
| TravelerService        | 8082 |
| PaymentService         | 8083 |
| BankServiceMock        | 8084 |
| TransitService         | 8085 |