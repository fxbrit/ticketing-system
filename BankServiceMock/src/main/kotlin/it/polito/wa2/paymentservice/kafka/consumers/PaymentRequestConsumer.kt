package it.polito.wa2.paymentservice.kafka.consumers

import it.polito.wa2.paymentservice.entities.PaymentResponse
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class PaymentRequestConsumer(
    @Value("inResponses") val topic: String,
    @Autowired
    @Qualifier("paymentResponseTemplate")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["outRequests"], groupId = "bnk")
    fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<Any, Any>) {
        logger.info("Incoming payment request from PaymentService{}", consumerRecord)


        val message: Message<PaymentResponse> = MessageBuilder
            .withPayload(PaymentResponse(1, 1))
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")

    }

}
