package it.polito.wa2.group03authenticationauthorization.services

import it.polito.wa2.group03authenticationauthorization.dtos.TicketPurchasedDTO
import it.polito.wa2.group03authenticationauthorization.dtos.TicketUserActionDTO
import it.polito.wa2.group03authenticationauthorization.dtos.toDTO
import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import it.polito.wa2.group03authenticationauthorization.enums.*
import it.polito.wa2.group03authenticationauthorization.repositories.TicketPurchasedRepository
import it.polito.wa2.group03authenticationauthorization.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.next
import java.util.*

@Service
class TicketsService {

    @Autowired
    lateinit var ticketsRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    @Value("\${jwt.outgoing-key}")
    lateinit var key: String

    fun getTicket(ticketId: UUID): TicketPurchasedDTO {
        return ticketsRepository.findById(ticketId).get().toDTO(key)
    }

    fun getTickets(userId: Long): List<TicketPurchasedDTO> {
        return ticketsRepository.getTicketsByUserId(userId).map { it.toDTO(key) }
    }

    fun createTickets(ticketOrder: TicketUserActionDTO): List<TicketPurchasedDTO> {
        val ticketList = mutableListOf<TicketPurchasedDTO>()

        val userProfile = userDetailsRepository.findById(ticketOrder.userId!!).orElseThrow()

        for (i in 1..ticketOrder.quantity) {
            var ticket = TicketPurchased(userProfile, null, null, ticketOrder.zones)
            addStartValidity(ticket, ticketOrder.ticketType)
            addEndValidity(ticket, ticketOrder.ticketType)

            ticket = ticketsRepository.save(ticket)
            ticketList.add(ticket.toDTO(key))
        }

        return ticketList
    }

    fun addStartValidity(ticket: TicketPurchased, type: String) {
        val start = when (type) {
            TicketTypeString[TicketTypes.ONE_WAY] -> LocalDateTime.now()
            TicketTypeString[TicketTypes.WEEKEND] -> LocalDateTime.now().with(next(DayOfWeek.SATURDAY))
                .truncatedTo(ChronoUnit.DAYS)

            TicketTypeString[TicketTypes.MONTHLY] -> LocalDateTime.now().with(firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS)

            else -> {
                LocalDateTime.now()
            }
        }

        ticket.startValidity = java.sql.Timestamp.valueOf(start)
    }

    fun addEndValidity(ticket: TicketPurchased, type: String) {
        val end = when (type) {
            TicketTypeString[TicketTypes.ONE_WAY] -> LocalDateTime.now().plusHours(1)
            TicketTypeString[TicketTypes.WEEKEND] -> LocalDateTime.now().with(next(DayOfWeek.MONDAY))
                .truncatedTo(ChronoUnit.DAYS)

            TicketTypeString[TicketTypes.MONTHLY] -> LocalDateTime.now().plusMonths(1).with(firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS)

            else -> {
                LocalDateTime.now()
            }
        }

        ticket.endValidity = java.sql.Timestamp.valueOf(end)
    }
}