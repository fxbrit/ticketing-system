package it.polito.wa2.group03authenticationauthorization.dtos

import it.polito.wa2.group03authenticationauthorization.entities.UserDetails

data class UserDetailsDTO(
    var userId: Long?,
    val name: String?,
    var role: String?,
    val address: String?,
    val dateOfBirth: String?,
    val telephoneNumber: String?,
    val tickets: List<TicketPurchasedDTO>?
)

// passing an empty JWS to the ticketDTO is a workaround but in this context we shouldn't ever really need it
fun UserDetails.toDTO() = UserDetailsDTO(userId, name, role, address, dateOfBirth, telephoneNumber, tickets.map { it.toDTO("") })
