package it.polito.wa2.ticketcatalogueservice.dto

import it.polito.wa2.ticketcatalogueservice.entities.Ticket

data class TicketDTO(
    val id: Long,
    val price: Float,
    val type: String,
    val reduction: String?,
    val max_age: Int?,
    val min_age: Int?
)

fun Ticket.toDTO() = TicketDTO(id, price, type, reduction, max_age, min_age)
