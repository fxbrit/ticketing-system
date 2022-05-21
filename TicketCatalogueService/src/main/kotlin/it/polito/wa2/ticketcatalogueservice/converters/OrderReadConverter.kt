package it.polito.wa2.ticketcatalogueservice.converters

import io.r2dbc.spi.Row
import it.polito.wa2.ticketcatalogueservice.entities.Order
import it.polito.wa2.ticketcatalogueservice.entities.PaymentInformation
import it.polito.wa2.ticketcatalogueservice.entities.Ticket
import it.polito.wa2.ticketcatalogueservice.entities.User
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.LocalDate

@ReadingConverter
class OrderReadConverter : Converter<Row, Order> {
    override fun convert(source: Row): Order {
        val ticket = Ticket(
            (source.get("id") as Int).toLong(),
            (source.get("price") as Double).toFloat(),
            source.get("type") as String
        )

        val user = User(
            (source.get("id") as Int).toLong(),
            source.get("email") as String,
            source.get("username") as String
        )

        val paymentInformation = PaymentInformation(
            (source.get("id") as Int).toLong(),
            source.get("creditcardnumber") as Int,
            source.get("cvv") as Int,
            source.get("expirationdate") as LocalDate,
            (source.get("userid") as Int).toLong()
        )

        return Order(
            (source.get("id") as Int).toLong(),
            (source.get("ticketid") as Int).toLong(),
            source.get("quantity") as Int,
            (source.get("paymentid") as Int).toLong(),
            (source.get("userid") as Int).toLong(),
            ticket,
            user,
            paymentInformation,
        )
    }


}