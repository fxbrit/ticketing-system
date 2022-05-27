package it.polito.wa2.ticketcatalogueservice.entities
import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentResponse(

    @JsonProperty("orderId")
    val orderId: Long,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */
    @JsonProperty("status")
    val status: Int

)
