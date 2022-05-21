package it.polito.wa2.paymentservice.kafka.consumers

import it.polito.wa2.paymentservice.entities.Payment
import it.polito.wa2.paymentservice.entities.PaymentBankRequest
import it.polito.wa2.paymentservice.entities.PaymentRequest
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
import java.util.*

@Component
class PaymentRequestConsumer(
    @Value(Topics.travelerToPayment) val topic: String,
    @Autowired
    @Qualifier("paymentRequestTemplate")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = [Topics.travelerToPayment], groupId = "ppr")
    suspend fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<Any, Any>) {

        /** receive from TicketCatalog... */
        logger.info("Incoming payment request {}", consumerRecord)

        /** ...update DB... */
        val request = consumerRecord.value() as PaymentRequest
        val payment = Payment(
            UUID.randomUUID(),
            request.orderId,
            request.userId,
            0
        )
        paymentRepository.save(payment)

        /**
         * ...and send to other internal services.
         */
        val paymentBankRequest =
            PaymentBankRequest(
                payment.paymentId,
                request.creditCardNumber,
                request.cvv,
                request.expirationDate,
                request.amount
            )
        logger.info("Sending payment request to Bank..")
        logger.info("The message to Kafka: {}", paymentBankRequest)

        this.forwardPaymentRequest(paymentBankRequest)

    }

    fun forwardPaymentRequest(request: PaymentBankRequest) {

        val message: Message<PaymentBankRequest> = MessageBuilder
            .withPayload(request)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")

    }

}
