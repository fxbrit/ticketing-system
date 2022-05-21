package it.polito.wa2.ticketcatalogueservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("tickets")
data class Ticket(
    @Id
    val id: Long,
    val price: Float,
    val type: String
)
