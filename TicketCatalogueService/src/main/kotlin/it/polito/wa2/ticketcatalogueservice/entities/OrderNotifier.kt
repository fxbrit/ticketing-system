package it.polito.wa2.ticketcatalogueservice.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderNotifier(

    @JsonProperty("ticketType")
    val ticketType: String,

    @JsonProperty("quantity")
    val quantity: Int,

    @JsonProperty("zones")
    val zones: String = "",

    @JsonProperty("userId")
    val userId: Long

)
