package it.polito.wa2.ticketcatalogueservice.Services

import it.polito.wa2.ticketcatalogueservice.Entities.Order
import it.polito.wa2.ticketcatalogueservice.Entities.Ticket
import it.polito.wa2.ticketcatalogueservice.Repositories.OrderRepository
import it.polito.wa2.ticketcatalogueservice.Repositories.TicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicketCatalogueService {
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    fun getAllOrders(): Flow<Order> {
        return orderRepository.findAllOrders()
    }

    fun getAllTickets(): Flow<Ticket> {
        return ticketRepository.findAll()
    }

    suspend fun createNewTicket(ticket: Ticket): Flow<Ticket> {
        val savedTicket = ticketRepository.save(ticket)
        return flow {
            emit(savedTicket)
        }
    }

    suspend fun getOrderById(id: Long): Order? {
        return withContext(Dispatchers.IO) {
            orderRepository.findOrderById(id).block()
        }
    }
}