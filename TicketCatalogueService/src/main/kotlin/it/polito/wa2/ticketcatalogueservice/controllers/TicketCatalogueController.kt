package it.polito.wa2.ticketcatalogueservice.controllers

import it.polito.wa2.ticketcatalogueservice.dto.PaymentBuyTicketDTO
import it.polito.wa2.ticketcatalogueservice.dto.UserDetailsDTO
import it.polito.wa2.ticketcatalogueservice.entities.Order
import it.polito.wa2.ticketcatalogueservice.entities.Ticket
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

@RestController
class TicketCatalogueController(@Value("\${traveler-service-endpoint}") travelerServiceEndpoint: String) {

    private val webClient: WebClient = WebClient.create(travelerServiceEndpoint)

    @Autowired
    lateinit var ticketCatalogueService: TicketCatalogueService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as Long }

    @GetMapping("/admin/orders")
    fun getAllOrders(): Flow<Order> {
        return ticketCatalogueService.getAllOrders()
    }

    @GetMapping("/admin/orders/{userId}")
    suspend fun getAllUserOrdersAdmin(@PathVariable userId: Long): Flow<Order> {
        return ticketCatalogueService.getAllUserOrders(userId)
    }

    @GetMapping("/tickets")
    suspend fun getAllTickets(): Flow<Ticket> {
        return ticketCatalogueService.getAllTickets()
    }

    @PostMapping("/admin/tickets")
    suspend fun addNewTicket(@RequestBody newTicket: Ticket): Flow<Ticket> {
        return ticketCatalogueService.createNewTicket(newTicket)
    }

    @PostMapping("/shop/{ticketId}")
    suspend fun buyTicket(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable ticketId: Long,
        @RequestBody paymentBuyInfo: PaymentBuyTicketDTO
    ): ResponseEntity<Order> {
        var userDetailsDTO : UserDetailsDTO? = null
        try {
             userDetailsDTO = webClient
                .get()
                .uri("/my/profile")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<UserDetailsDTO>()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }


        val userId = principal.awaitSingle()

        // Check if the user is eligible
        // if (userDetailsDTO.dateOfBirth)

        val order = ticketCatalogueService.buyTicket(userId, ticketId, paymentBuyInfo)
        return ResponseEntity.status(HttpStatus.OK).body(order)
    }

    @GetMapping("/orders")
     suspend fun getAllUserOrders(): Flow<Order> {
        val userId = principal.awaitSingle()
        return ticketCatalogueService.getAllUserOrders(userId)
    }

    @GetMapping("/orders/{orderId}")
    suspend fun getOrderById(@PathVariable orderId: Long): Order? {
        val userId = principal.awaitSingle()
        return ticketCatalogueService.getOrderById(orderId, userId)
    }
}