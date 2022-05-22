package it.polito.wa2.ticketcatalogueservice.dto

import it.polito.wa2.ticketcatalogueservice.entities.PaymentInformation

data class PaymentBuyTicketDTO (
        val amount: Int,
        val paymentInformations: PaymentInformation)