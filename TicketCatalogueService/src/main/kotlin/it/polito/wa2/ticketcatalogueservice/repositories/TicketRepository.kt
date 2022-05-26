package it.polito.wa2.ticketcatalogueservice.repositories

import it.polito.wa2.ticketcatalogueservice.entities.Ticket
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: CoroutineCrudRepository<Ticket, Long>{

    suspend fun findTicketById(id: Long): Ticket?

}
