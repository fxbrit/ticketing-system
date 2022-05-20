package it.polito.wa2.paymentservice.kafka.consumers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentRequestConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["inRequests"], groupId = "ppr")
    fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<Any, Any>) {
        logger.info("Incoming payment request {}", consumerRecord)
    }

}
