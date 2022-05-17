package it.polito.wa2.group03authenticationauthorization.services

import it.polito.wa2.group03authenticationauthorization.dtos.TicketPurchasedDTO
import it.polito.wa2.group03authenticationauthorization.dtos.TicketUserActionDTO
import it.polito.wa2.group03authenticationauthorization.dtos.toDTO
import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import it.polito.wa2.group03authenticationauthorization.repositories.TicketPurchasedRepository
import it.polito.wa2.group03authenticationauthorization.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicketsService {

    @Autowired
    lateinit var ticketsRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    fun getTickets(userId: Long): List<TicketPurchasedDTO> {
        return ticketsRepository.getTicketsByUserId(userId).map { it.toDTO() }
    }

    fun createTickets(ticketOrder: TicketUserActionDTO): List<TicketPurchasedDTO> {
        val ticketList = mutableListOf<TicketPurchasedDTO>()
        val userProfile = userDetailsRepository.findById(ticketOrder.userId!!).orElseThrow()
        for (i in 1..ticketOrder.quantity) {
            var ticket = TicketPurchased(userProfile, ticketOrder.zones)
            ticket = ticketsRepository.save(ticket)
            ticketList.add(ticket.toDTO())
        }

        return ticketList
    }
}