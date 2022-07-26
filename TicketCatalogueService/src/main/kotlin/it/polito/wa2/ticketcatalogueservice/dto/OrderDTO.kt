package it.polito.wa2.ticketcatalogueservice.dto

import it.polito.wa2.ticketcatalogueservice.entities.Order
import java.sql.Timestamp

data class OrderDTO(
    val id: Long?,
    val ticketId: Long,
    val quantity: Int,
    val userId: Long,
    val time: Timestamp,
    val status: String
)

fun Order.toDTO() = OrderDTO(id, ticketId, quantity, userId, time, status)
