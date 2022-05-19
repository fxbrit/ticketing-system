package it.polito.wa2.ticketcatalogueservice.Controllers

import it.polito.wa2.ticketcatalogueservice.Entities.Order
import it.polito.wa2.ticketcatalogueservice.Entities.Ticket
import it.polito.wa2.ticketcatalogueservice.Services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*

@RestController
class TicketCatalogueController {
    @Autowired
    lateinit var ticketCatalogueService: TicketCatalogueService

    @GetMapping("/admin/orders")
    fun getAllOrders(): Flow<Order> {
        return ticketCatalogueService.getAllOrders()
    }

    @GetMapping("/admin/orders/{id}")
    suspend fun getOrderById(@PathVariable id: Long, response: ServerHttpResponse): Order? {
        return ticketCatalogueService.getOrderById(id)
    }

    @GetMapping("/tickets")
    fun getAllTickets(): Flow<Ticket> {
        return ticketCatalogueService.getAllTickets()
    }

    @PostMapping("/admin/tickets")
    suspend fun addNewTicket(@RequestBody newTicket: Ticket): Flow<Ticket> {
        return ticketCatalogueService.createNewTicket(newTicket)
    }
}