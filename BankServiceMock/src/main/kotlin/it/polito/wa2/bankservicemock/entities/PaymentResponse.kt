package it.polito.wa2.bankservicemock.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class PaymentResponse(

    @JsonProperty("paymentId")
    val paymentId: Long,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */
    @JsonProperty("status")
    val status: Int

)
