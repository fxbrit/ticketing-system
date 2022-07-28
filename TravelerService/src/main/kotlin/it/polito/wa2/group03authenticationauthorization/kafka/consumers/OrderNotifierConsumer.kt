package it.polito.wa2.group03authenticationauthorization.kafka.consumers

import it.polito.wa2.group03authenticationauthorization.dtos.TicketUserActionDTO
import it.polito.wa2.group03authenticationauthorization.entities.OrderNotifier
import it.polito.wa2.group03authenticationauthorization.kafka.Topics
import it.polito.wa2.group03authenticationauthorization.services.TicketsService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderNotifierConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var ticketsService: TicketsService

    @KafkaListener(
        containerFactory = "orderNotifierListenerContainerFactory",
        topics = [Topics.catalogueToTraveler],
        groupId = "tra"
    )
    fun listenFromTicketCatalogueService(consumerRecord: ConsumerRecord<Any, OrderNotifier>) {

        /** receive from TicketCatalogue ... */
        logger.info("Incoming OrderNotifier {}", consumerRecord)

        val response = consumerRecord.value()

        ticketsService.createTickets(
            TicketUserActionDTO(
                "buy_tickets",
                response.ticketType,
                response.quantity,
                response.zones,
                response.userId
            )
        )

        logger.info("The message to Kafka: {}", consumerRecord.value())

    }

}
