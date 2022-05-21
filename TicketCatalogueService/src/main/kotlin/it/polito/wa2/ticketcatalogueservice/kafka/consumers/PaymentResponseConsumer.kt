package it.polito.wa2.ticketcatalogueservice.kafka.consumers

import it.polito.wa2.ticketcatalogueservice.kafka.Topics
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class PaymentResponseConsumer(
    @Value(Topics.paymentToTraveler) val topic: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = [Topics.paymentToTraveler], groupId = "ctl")
    suspend fun listenFromPaymentService(consumerRecord: ConsumerRecord<Any, Any>) {

        /** receive from PaymentService ... */
        logger.info("Incoming payment response {}", consumerRecord)

        /** TODO: Update the order in the DB with the status received by the PaymentService ... */

    }

}
