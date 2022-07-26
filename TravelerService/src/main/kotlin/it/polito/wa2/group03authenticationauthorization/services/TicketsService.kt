package it.polito.wa2.group03authenticationauthorization.services

import it.polito.wa2.group03authenticationauthorization.dtos.TicketPurchasedDTO
import it.polito.wa2.group03authenticationauthorization.dtos.TicketUserActionDTO
import it.polito.wa2.group03authenticationauthorization.dtos.toDTO
import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import it.polito.wa2.group03authenticationauthorization.repositories.TicketPurchasedRepository
import it.polito.wa2.group03authenticationauthorization.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters.next

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
            var ticket = TicketPurchased(userProfile, null, null, ticketOrder.zones)
            addStartValidity(ticket, ticketOrder.ticketType)
            addEndValidity(ticket, ticketOrder.ticketType)

            ticket = ticketsRepository.save(ticket)
            ticketList.add(ticket.toDTO())
        }

        return ticketList
    }

    fun addStartValidity(ticket: TicketPurchased, type: String) {
        val start = when (type) {
            "one way" -> LocalDateTime.now()
            "weekend" -> LocalDateTime.now().with(next(DayOfWeek.SATURDAY)).truncatedTo(ChronoUnit.DAYS)
            "monthly" -> LocalDateTime.now().truncatedTo(ChronoUnit.MONTHS)
            else -> {LocalDateTime.now()}
        }

        ticket.startValidity = java.sql.Timestamp.valueOf(start)
    }

    fun addEndValidity(ticket: TicketPurchased, type: String) {
        val end = when (type) {
            "one way" -> LocalDateTime.now().plusHours(1)
            "weekend" -> LocalDateTime.now().with(next(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS)
            "monthly" -> LocalDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.MONTHS)
            else -> {LocalDateTime.now()}
        }

        ticket.endValidity = java.sql.Timestamp.valueOf(end)
    }
}