package it.polito.wa2.ticketcatalogueservice.Services

import it.polito.wa2.ticketcatalogueservice.Entities.Order
import it.polito.wa2.ticketcatalogueservice.Repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicketCatalogueService {
    @Autowired
    lateinit var orderRepository: OrderRepository

    fun getAllOrders(): Flow<Order> {
        return orderRepository.findAllOrders()
    }
}