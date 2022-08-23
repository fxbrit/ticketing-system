package it.polito.wa2.turnstileservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("turnstiles")
data class Turnstile(
    @Id
    var id: Long?,
    val username: String,
    var password: String,
    var salt: String?
)
