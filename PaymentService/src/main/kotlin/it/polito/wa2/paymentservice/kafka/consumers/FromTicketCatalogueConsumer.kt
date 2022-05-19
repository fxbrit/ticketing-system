package it.polito.wa2.paymentservice.kafka.consumers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class FromTicketCatalogueConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["fromTicketCatalogue"], groupId = "ppr")
    fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received from TicketCatalogueService {}", consumerRecord)
        ack.acknowledge()
    }

}
