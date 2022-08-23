package it.polito.wa2.turnstileservice.dto

import java.util.*

interface TransitDataDTO

data class TransitDataValidDTO(
    // Fields copied from TicketPurchasedDTO, representing a ticket scanned
    val ticketID: UUID,          // Ticket ID
    val issuedAt: Date,          // Issued at
    val startValidity: Date,     // Timestamp of start validity
    val endValidity: Date,       // Timestamp of end validity
    val zones: String,           // Zone ID
    val userID: Long,            // User ID
    val jws: String,             // Encoding of ticket as JWT
    val turnstileID: Long        // Turnstile ID
) : TransitDataDTO

data class TransitDataErrorDTO(
    val errorMessage: String
) : TransitDataDTO