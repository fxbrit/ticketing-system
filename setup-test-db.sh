#!/bin/bash

export PGPASSWORD=postgres

echo "Creating LoginService database"
createdb -h localhost -p 5432 -U postgres registration
# TODO: add init script

echo "Creating TicketCatalogueService database"
createdb -h localhost -p 5432 -U postgres ordersdb
psql -h localhost -p 5432 -U postgres --dbname=ordersdb -f TicketCatalogueService/src/main/kotlin/it/polito/wa2/ticketcatalogueservice/SQL/ddl.sql

echo "Creating PaymentService database"
createdb -h localhost -p 5432 -U postgres payments
psql -h localhost -p 5432 -U postgres --dbname=payments -f PaymentService/src/main/kotlin/it/polito/wa2/paymentservice/sql/ddl.sql

echo "Creating TravelerService database"
createdb -h localhost -p 5432 -U postgres tickets
# TODO: add init script

