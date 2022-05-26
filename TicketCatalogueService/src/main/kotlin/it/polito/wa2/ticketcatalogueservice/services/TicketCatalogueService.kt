package it.polito.wa2.ticketcatalogueservice.services

import it.polito.wa2.ticketcatalogueservice.dto.PaymentBuyTicketDTO
import it.polito.wa2.ticketcatalogueservice.entities.Order
import it.polito.wa2.ticketcatalogueservice.entities.PaymentRequest
import it.polito.wa2.ticketcatalogueservice.entities.Ticket
import it.polito.wa2.ticketcatalogueservice.kafka.Topics
import it.polito.wa2.ticketcatalogueservice.repositories.OrderRepository
import it.polito.wa2.ticketcatalogueservice.repositories.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class TicketCatalogueService(
    @Autowired
    @Qualifier("paymentRequestTemplate")
    private val paymentRequestTemplate: KafkaTemplate<String, Any>
) {


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

    suspend fun getTicketById(id: Long): Ticket? {
        return ticketRepository.findTicketById(id)
    }

    fun getAllUserOrders(id: Long): Flow<Order> {
        return orderRepository.findUserOrders(id)
    }

    suspend fun buyTicket(userId: Long, ticketId: Long, paymentBuyTicketDTO: PaymentBuyTicketDTO) : Order {

        val order = orderRepository.save(
            Order(
                null, ticketId, paymentBuyTicketDTO.amount, userId, "PENDING", null, null
            )
        )

        val message: Message<PaymentRequest> = MessageBuilder
            .withPayload(PaymentRequest(
                order.id!!,
                userId,
                paymentBuyTicketDTO.paymentInformations.creditCardNumber,
                paymentBuyTicketDTO.paymentInformations.cvv,
                paymentBuyTicketDTO.paymentInformations.expirationDate,
                paymentBuyTicketDTO.amount,
            ))
            .setHeader(KafkaHeaders.TOPIC, Topics.catalogueToPayment)
            .build()

        paymentRequestTemplate.send(message)

        return order
    }
}