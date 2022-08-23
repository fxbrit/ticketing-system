#!/bin/sh

export PGPASSWORD=postgres

dropdb -h localhost -p 5432 -U postgres registration
dropdb -h localhost -p 5432 -U postgres orders
dropdb -h localhost -p 5432 -U postgres payments
dropdb -h localhost -p 5432 -U postgres tickets
dropdb -h localhost -p 5432 -U postgres transits

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
psql -h localhost -p 5432 -U postgres --dbname=tickets -f TravelerService/src/main/kotlin/it/polito/wa2/group03authenticationauthorization/sql/ddl.sql

echo "Creating TransitService database"
createdb -h localhost -p 5432 -U postgres transits
# TODO: add init script, if necessary

echo "Creating TurnstilesService database"
createdb -h localhost -p 5432 -U postgres turnstiles
psql -h localhost -p 5432 -U postgres --dbname=turnstiles -f TurnstileService/src/main/kotlin/it/polito/wa2/turnstileservice/sql/ddl.sql
