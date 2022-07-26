package it.polito.wa2.group03authenticationauthorization.entities

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class TicketPurchased(@ManyToOne var ticketOwner: UserDetails,
                      var startValidity: Date?,
                      var endValidity: Date?,
                      var zoneId: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var ticketId: UUID? = null

    /** according to guidelines ticket should be "issued now" */
    @Temporal(TemporalType.TIMESTAMP)
    var issuedAt: Date = java.sql.Timestamp.valueOf(LocalDateTime.now())

}
