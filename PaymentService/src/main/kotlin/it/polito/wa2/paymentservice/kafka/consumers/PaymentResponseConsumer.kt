package it.polito.wa2.paymentservice.kafka.consumers

import it.polito.wa2.paymentservice.entities.PaymentResponse
import it.polito.wa2.paymentservice.kafka.Topics
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
    @Value(Topics.paymentToTraveler) val topic: String,
    @Autowired
    @Qualifier("paymentResponseTemplate")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = [Topics.bankToPayment], groupId = "ppr")
    fun listenFromBank(consumerRecord: ConsumerRecord<Any, Any>) {

        /** receive from Bank... */
        logger.info("Incoming payment response {}", consumerRecord)

        /**
         * ...and send to other internal services.
         * TODO: response status should be updated in the DB
         */
        logger.info("Sending payment response out..")
        logger.info("The message to Kafka: {}", consumerRecord.value())

        this.forwardPaymentResponse()

    }

    fun forwardPaymentResponse() {

        /** TODO: payload should be received on call */
        val message: Message<PaymentResponse> = MessageBuilder
            .withPayload(PaymentResponse(1, 1))
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")

    }

}
