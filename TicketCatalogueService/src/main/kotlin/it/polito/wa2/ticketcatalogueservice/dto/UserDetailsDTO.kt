package it.polito.wa2.ticketcatalogueservice.dto


data class UserDetailsDTO(
    var userId: Long?,
    val name: String?,
    val role: String?,
    val address: String?,
    val dateOfBirth: String?,
    val telephoneNumber: String?,
)

