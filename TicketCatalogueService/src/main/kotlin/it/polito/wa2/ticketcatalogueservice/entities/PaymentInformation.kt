package it.polito.wa2.ticketcatalogueservice.entities

import java.time.LocalDate

data class PaymentInformation(
    val creditCardNumber: Int,
    val cvv: Int,
    val expirationDate: String
)
