package it.polito.wa2.transitservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.Date

@Table("transits")
data class Transit(
    @Id
    val id: Long,
    val ticketUUID: String,
    val userID: Long,
    val turnstileID: Long,
    val time: Date
)
