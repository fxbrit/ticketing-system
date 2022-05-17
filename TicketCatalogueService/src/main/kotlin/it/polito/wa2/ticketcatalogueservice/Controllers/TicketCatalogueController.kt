package it.polito.wa2.ticketcatalogueservice.Controllers

import it.polito.wa2.ticketcatalogueservice.Entities.Order
import it.polito.wa2.ticketcatalogueservice.Services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TicketCatalogueController {
    @Autowired
    lateinit var ticketCatalogueService: TicketCatalogueService

    @GetMapping("/admin/orders")
    fun getAllOrders(): Flow<Order> {
        return ticketCatalogueService.getAllOrders()
    }
}