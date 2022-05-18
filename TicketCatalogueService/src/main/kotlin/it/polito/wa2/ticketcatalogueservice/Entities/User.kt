package it.polito.wa2.ticketcatalogueservice.Entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    val id: Long,
    val username: String,
    val email: String
)
