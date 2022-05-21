package it.polito.wa2.paymentservice.kafka.consumers

import it.polito.wa2.paymentservice.entities.PaymentBankResponse
import it.polito.wa2.paymentservice.entities.PaymentResponse
import it.polito.wa2.paymentservice.kafka.Topics
import it.polito.wa2.paymentservice.repositories.PaymentRepository
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

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = [Topics.bankToPayment], groupId = "ppr")
    suspend fun listenFromBank(consumerRecord: ConsumerRecord<Any, Any>) {

        /** receive from Bank... */
        logger.info("Incoming payment response {}", consumerRecord)

        /** ...update DB... */
        val response = consumerRecord.value() as PaymentBankResponse
        val payment = paymentRepository.findById(response.paymentId)
        if (payment != null) {
            payment.status = response.status
            paymentRepository.save(payment)
        }

        /**
         * ...and send to other internal services.
         */
        val paymentResponse = payment?.let {
            PaymentResponse(
                it.orderId,
                response.status
            )
        }
        logger.info("Sending payment response out..")
        logger.info("The message to Kafka: {}", consumerRecord.value())

        if (paymentResponse != null) {
            this.forwardPaymentResponse(paymentResponse)
        }

    }

    fun forwardPaymentResponse(response: PaymentResponse) {

        val message: Message<PaymentResponse> = MessageBuilder
            .withPayload(response)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")

    }

}
