package it.polito.wa2.group03authenticationauthorization.dtos

import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import java.util.*

data class TicketPurchasedDTO(
    val sub: UUID?,         // Ticket ID
    val iat: Date?,         // Issued at
    val sta: Date?,         // Timestamp of start validity
    val exp: Date?,         // Timestamp of end validity
    val zid: String,        // Zone ID
    var jws: String?        // Encoding of ticket as JWT
)

fun TicketPurchased.toDTO(jws: String?): TicketPurchasedDTO {
    return TicketPurchasedDTO(ticketId, issuedAt, startValidity, endValidity, zoneId, jws)
}
