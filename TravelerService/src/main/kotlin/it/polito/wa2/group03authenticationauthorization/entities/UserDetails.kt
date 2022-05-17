package it.polito.wa2.group03authenticationauthorization.entities

import javax.persistence.*

@Entity
class UserDetails(
    @Id var userId: Long,
    var name: String,
    var role: String,
    var address: String?,
    var dateOfBirth: String?,
    var telephoneNumber: String?,
) {

    @OneToMany(mappedBy = "ticketOwner")
    var tickets: List<TicketPurchased> = listOf()

}
