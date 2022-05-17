package it.polito.wa2.group03authenticationauthorization.repositories

import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TicketPurchasedRepository : CrudRepository<TicketPurchased, UUID> {

    @Query("SELECT t FROM TicketPurchased t WHERE t.ticketOwner.userId = ?1")
    fun getTicketsByUserId(userId: Long): List<TicketPurchased>
}

