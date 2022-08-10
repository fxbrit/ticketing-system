#!/bin/bash

export PGPASSWORD=postgres

echo "Creating LoginService database"
createdb -h localhost -p 5432 -U postgres registration
psql -h localhost -p 5432 -U postgres --dbname=registration -f LoginService/src/main/kotlin/it/polito/wa2/group03userregistration/sql/ddl.sql

echo "Creating TicketCatalogueService database"
createdb -h localhost -p 5432 -U postgres orders
psql -h localhost -p 5432 -U postgres --dbname=orders -f TicketCatalogueService/src/main/kotlin/it/polito/wa2/ticketcatalogueservice/SQL/ddl.sql

echo "Creating PaymentService database"
createdb -h localhost -p 5432 -U postgres payments
psql -h localhost -p 5432 -U postgres --dbname=payments -f PaymentService/src/main/kotlin/it/polito/wa2/paymentservice/sql/ddl.sql

echo "Creating TravelerService database"
createdb -h localhost -p 5432 -U postgres tickets
# TODO: add init script

