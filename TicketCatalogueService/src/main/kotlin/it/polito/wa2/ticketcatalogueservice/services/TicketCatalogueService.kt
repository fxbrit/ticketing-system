package it.polito.wa2.ticketcatalogueservice.services

import it.polito.wa2.ticketcatalogueservice.entities.Order
import it.polito.wa2.ticketcatalogueservice.entities.Ticket
import it.polito.wa2.ticketcatalogueservice.repositories.OrderRepository
import it.polito.wa2.ticketcatalogueservice.repositories.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    suspend fun getOrderById(orderId: Long, userId: Long): Order? {
        return orderRepository.findOrderById(orderId, userId)
    }

    fun getAllUserOrders(id: Long): Flow<Order> {
        return orderRepository.findUserOrders(id)
    }
}