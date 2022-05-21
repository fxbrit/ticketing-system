package it.polito.wa2.ticketcatalogueservice.Controllers

import it.polito.wa2.ticketcatalogueservice.Entities.Order
import it.polito.wa2.ticketcatalogueservice.Entities.Ticket
import it.polito.wa2.ticketcatalogueservice.Services.TicketCatalogueService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class TicketCatalogueController {
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