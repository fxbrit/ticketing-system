package it.polito.wa2.ticketcatalogueservice.converters

import io.r2dbc.spi.Row
import it.polito.wa2.ticketcatalogueservice.entities.Order
import it.polito.wa2.ticketcatalogueservice.entities.Ticket
import it.polito.wa2.ticketcatalogueservice.entities.User
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class OrderReadConverter : Converter<Row, Order> {
    override fun convert(source: Row): Order {
        val ticket = Ticket(
            (source.get("ticketid") as Int).toLong(),
            (source.get("price") as Double).toFloat(),
            source.get("type") as String,
            source.get("max_age") as Int?,
            source.get("min_age") as Int?
        )

        val user = User(
            (source.get("userid") as Int).toLong(),
            source.get("email") as String,
            source.get("username") as String
        )

        return Order(
            (source.get("id") as Int).toLong(),
            (source.get("ticketid") as Int).toLong(),
            source.get("quantity") as Int,
            (source.get("userid") as Int).toLong(),
            source.get("status") as String,
            ticket,
            user
        )
    }


}