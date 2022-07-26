package it.polito.wa2.ticketcatalogueservice.services

import it.polito.wa2.ticketcatalogueservice.dto.OrderDTO
import it.polito.wa2.ticketcatalogueservice.dto.PaymentBuyTicketDTO
import it.polito.wa2.ticketcatalogueservice.dto.TicketDTO
import it.polito.wa2.ticketcatalogueservice.dto.toDTO
import it.polito.wa2.ticketcatalogueservice.entities.Order
import it.polito.wa2.ticketcatalogueservice.entities.PaymentRequest
import it.polito.wa2.ticketcatalogueservice.entities.Ticket
import it.polito.wa2.ticketcatalogueservice.kafka.Topics
import it.polito.wa2.ticketcatalogueservice.repositories.OrderRepository
import it.polito.wa2.ticketcatalogueservice.repositories.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    fun getAllOrders(): Flow<OrderDTO> {
        return orderRepository.findAllOrders().map { it.toDTO() }
    }

    fun getAllTickets(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    suspend fun createNewTicket(ticketDTO: TicketDTO): Flow<TicketDTO> {
        val ticket = Ticket(ticketDTO.id, ticketDTO.price, ticketDTO.type, ticketDTO.max_age, ticketDTO.min_age)
        val savedTicket = ticketRepository.save(ticket).toDTO()
        return flow {
            emit(savedTicket)
        }
    }

    suspend fun getOrderById(orderId: Long, userId: Long): OrderDTO? {
        return orderRepository.findOrderById(orderId, userId)?.toDTO()
    }

    suspend fun getTicketById(id: Long): TicketDTO? {
        return ticketRepository.findTicketById(id)?.toDTO()
    }

    fun getAllUserOrders(id: Long): Flow<OrderDTO> {
        return orderRepository.findUserOrders(id).map { it.toDTO() }
    }

    suspend fun buyTicket(userId: Long, ticketId: Long, paymentBuyTicketDTO: PaymentBuyTicketDTO): OrderDTO {

        val order = orderRepository.save(
            Order(null, ticketId, paymentBuyTicketDTO.amount, userId, "PENDING", null)
        )

        val message: Message<PaymentRequest> = MessageBuilder
            .withPayload(
                PaymentRequest(
                    order.id!!,
                    userId,
                    paymentBuyTicketDTO.paymentInformations.creditCardNumber,
                    paymentBuyTicketDTO.paymentInformations.cvv,
                    paymentBuyTicketDTO.paymentInformations.expirationDate,
                    paymentBuyTicketDTO.amount,
                )
            )
            .setHeader(KafkaHeaders.TOPIC, Topics.catalogueToPayment)
            .build()

        paymentRequestTemplate.send(message)

        return order.toDTO()
    }
}