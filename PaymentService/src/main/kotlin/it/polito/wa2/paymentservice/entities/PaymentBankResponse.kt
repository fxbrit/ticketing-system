package it.polito.wa2.paymentservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class PaymentBankResponse(

    @JsonProperty("paymentId")
    val paymentId: UUID,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */
    @JsonProperty("status")
    val status: Int

)
