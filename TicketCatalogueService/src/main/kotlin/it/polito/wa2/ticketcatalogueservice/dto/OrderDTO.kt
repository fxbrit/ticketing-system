package it.polito.wa2.ticketcatalogueservice.dto

import it.polito.wa2.ticketcatalogueservice.entities.Order

data class OrderDTO(
    val id: Long?,
    val ticketId: Long,
    val quantity: Int,
    val userId: Long,
    val status: String
)

fun Order.toDTO() = OrderDTO(id, ticketId, quantity, userId, status)
