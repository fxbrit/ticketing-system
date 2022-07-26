package it.polito.wa2.ticketcatalogueservice.controllers

import it.polito.wa2.ticketcatalogueservice.dto.OrderDTO
import it.polito.wa2.ticketcatalogueservice.dto.PaymentBuyTicketDTO
import it.polito.wa2.ticketcatalogueservice.dto.TicketDTO
import it.polito.wa2.ticketcatalogueservice.dto.UserDetailsDTO
import it.polito.wa2.ticketcatalogueservice.services.TicketCatalogueService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RestController
class TicketCatalogueController(@Value("\${traveler-service-endpoint}") travelerServiceEndpoint: String) {

    private val webClient: WebClient = WebClient.create(travelerServiceEndpoint)

    @Autowired
    lateinit var ticketCatalogueService: TicketCatalogueService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as Long }

    @GetMapping("/admin/orders")
    fun getAllOrders(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Flow<OrderDTO> {
        return ticketCatalogueService.getAllOrders(startDate, endDate)
    }

    @GetMapping("/admin/orders/{userId}")
    suspend fun getAllUserOrdersAdmin(
        @PathVariable userId: Long,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Flow<OrderDTO> {
        return ticketCatalogueService.getAllUserOrders(userId, startDate, endDate)
    }

    @GetMapping("/tickets")
    suspend fun getAllTickets(): Flow<TicketDTO> {
        return ticketCatalogueService.getAllTickets()
    }

    @PostMapping("/admin/tickets")
    suspend fun addNewTicket(@RequestBody newTicket: TicketDTO): Flow<TicketDTO> {
        return ticketCatalogueService.createNewTicket(newTicket)
    }

    @PostMapping("/shop/{ticketId}")
    suspend fun buyTicket(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable ticketId: Long,
        @RequestBody paymentBuyInfo: PaymentBuyTicketDTO
    ): ResponseEntity<OrderDTO> {
        val userDetailsDTO: UserDetailsDTO?
        try {
            userDetailsDTO = webClient
                .get()
                .uri("/my/profile")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        val userId = principal.awaitSingle()

        val ticket = ticketCatalogueService.getTicketById(ticketId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        // Check if the user is eligible
        val dateOfBirth = LocalDate.parse(userDetailsDTO.dateOfBirth, DateTimeFormatter.ISO_DATE)
        val age = (Period.between(LocalDate.now(), dateOfBirth).toTotalMonths()) / 12
        if (
            ((ticket.max_age != null) && (ticket.max_age < age)) || (((ticket.min_age != null) && (ticket.min_age > age)))
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val order = ticketCatalogueService.buyTicket(userId, ticketId, paymentBuyInfo)
        return ResponseEntity.status(HttpStatus.OK).body(order)
    }

    @GetMapping("/orders")
    suspend fun getAllUserOrders(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Flow<OrderDTO> {
        val userId = principal.awaitSingle()
        return ticketCatalogueService.getAllUserOrders(userId, startDate, endDate)
    }

    @GetMapping("/orders/{orderId}")
    suspend fun getOrderById(@PathVariable orderId: Long): OrderDTO? {
        val userId = principal.awaitSingle()
        return ticketCatalogueService.getOrderById(orderId, userId)
    }
}