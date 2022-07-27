package it.polito.wa2.ticketcatalogueservice.kafka.consumers

import it.polito.wa2.ticketcatalogueservice.entities.OrderNotifier
import it.polito.wa2.ticketcatalogueservice.entities.PaymentResponse
import it.polito.wa2.ticketcatalogueservice.kafka.Topics
import it.polito.wa2.ticketcatalogueservice.repositories.OrderRepository
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
class PaymentResponseConsumer(
    @Value(Topics.catalogueToTraveler) val topic: String,
    @Autowired
    @Qualifier("orderNotifierTemplate")
    private val kafkaTemplate: KafkaTemplate<String, OrderNotifier>
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var ordersRepository: OrderRepository


    @KafkaListener(
        containerFactory = "paymentResponseListenerContainerFactory",
        topics = [Topics.paymentToCatalogue],
        groupId = "ctl"
    )
    fun listenFromPaymentService(consumerRecord: ConsumerRecord<Any, PaymentResponse>) {

        /** receive from PaymentService ... */
        logger.info("Incoming payment response {}", consumerRecord)

        val response = consumerRecord.value()

        runBlocking {

            // Update the corresponding order in the database
            val targetOrder = ordersRepository.findOrderById(response.orderId)
            if (targetOrder != null) {
                logger.info("Received payment response for order {}, status = {}", response.orderId, response.status)
                targetOrder.status = if (response.status == 1) "COMPLETED" else "ERROR"
                ordersRepository.save(targetOrder)
            } else {
                logger.error("Received payment response for non-existing order {}", response.orderId)
            }

            // Forward the response to TravelerService
            val orderNotifier = targetOrder?.let {
                it.ticket?.let { it1 ->
                    OrderNotifier(
                        it1.type,
                        it.quantity,
                        "",
                        it.userId
                    )
                }
            }
            logger.info("Sending order notifier out..")
            logger.info("The message to Kafka: {}", consumerRecord.value())

            if (orderNotifier != null && response.status == 1) {
                forwardOrderNotifier(orderNotifier)
            }

        }

    }

    fun forwardOrderNotifier(response: OrderNotifier) {

        val message: Message<OrderNotifier> = MessageBuilder
            .withPayload(response)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")

    }

}
