# PaymentService

### Settings up the database
Start postgres:
```
docker run --name postgres -e POSTGRES_PASSWORD=<password> -d -p 5432:5432 -v <path-to-project>/database/:/var/lib/postgresql/data postgres
```

Then create the database and connect to it:
```
createdb -h localhost -p 5432 -U postgres payments
psql -h localhost -p 5432 -U postgres payments 
```

Finally, to create the table:
```postgres-sql
CREATE TABLE payment(
    paymentid UUID PRIMARY KEY NOT NULL,
    orderid INT NOT NULL,
    userid INT NOT NULL,
    status INT
);
```

Verify table creation with `\d payments` and exit with `\q`.

### Starting Kafka
From the root directory of PaymentService run:
```
docker-compose up
```