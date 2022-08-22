package it.polito.wa2.turnstileservice.dto

import java.util.*

interface TransitData

data class TransitDataDTO(
    // Fields copied from TicketPurchasedDTO, representing a ticket scanned
    val sub: UUID,          // Ticket ID
    val iat: Date,          // Issued at
    val sta: Date,          // Timestamp of start validity
    val exp: Date,          // Timestamp of end validity
    val zid: String,        // Zone ID
    val uid: Long,          // User ID
    val jws: String,        // Encoding of ticket as JWT

    val turnstileID: Long
) : TransitData

data class TransitDataErrorDTO(
    val errorMessage: String,
    val data: TransitDataDTO
) : TransitData