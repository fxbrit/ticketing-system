package it.polito.wa2.paymentservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class PaymentBankRequest(

    @JsonProperty("paymentId")
    val paymentId: UUID,

    @JsonProperty("creditCardNumber")
    val creditCardNumber: Int,

    @JsonProperty("cvv")
    val cvv: Int,

    @JsonProperty("expirationDate")
    val expirationDate: String,

    @JsonProperty("amount")
    val amount: Int

)
