package it.polito.wa2.paymentservice.kafka.consumers

import it.polito.wa2.paymentservice.entities.Payment
import it.polito.wa2.paymentservice.entities.PaymentBankRequest
import it.polito.wa2.paymentservice.entities.PaymentRequest
import it.polito.wa2.paymentservice.kafka.Topics
import it.polito.wa2.paymentservice.repositories.PaymentRepository
import kotlinx.coroutines.runBlocking
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
class PaymentRequestConsumer(
    @Value(Topics.paymentToBank) val topic: String,
    @Autowired
    @Qualifier("paymentRequestTemplate")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        containerFactory = "paymentRequestListenerContainerFactory", 
        topics = [Topics.catalogueToPayment], 
        groupId = "ppr"
    )
    fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<String, PaymentRequest>) {

        /** receive from TicketCatalog... */
        logger.info("Incoming payment request {}", consumerRecord)

        /** ...update DB... */
        val request = consumerRecord.value()
        val payment = Payment(
            null,
            request.orderId,
            request.userId,
            0
        )

        // TODO: Check how to execute this in a coroutine context
        runBlocking {
            val savedPayment = paymentRepository.save(payment)
            /**
             * ...and send to other internal services.
             */
            val paymentBankRequest =
                PaymentBankRequest(
                    savedPayment.paymentId!!,
                    request.creditCardNumber,
                    request.cvv,
                    request.expirationDate,
                    request.amount
                )
            logger.info("Sending payment request to Bank..")
            logger.info("The message to Kafka: {}", paymentBankRequest)

            val message: Message<PaymentBankRequest> = MessageBuilder
                .withPayload(paymentBankRequest)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()
            kafkaTemplate.send(message)
            logger.info("Message sent with success")
        }

    }

    fun forwardPaymentRequest(request: PaymentBankRequest) {


    }

}
