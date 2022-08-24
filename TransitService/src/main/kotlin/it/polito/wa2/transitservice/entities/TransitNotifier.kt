package it.polito.wa2.transitservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class TransitNotifier(

    @JsonProperty("userID")
    val userID: Long,

    @JsonProperty("ticketID")
    val ticketID: UUID,

    @JsonProperty("time")
    val time: LocalDateTime,

    @JsonProperty("turnstileID")
    val turnstileID: Long

)
