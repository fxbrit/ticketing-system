package it.polito.wa2.paymentservice.controller

import it.polito.wa2.paymentservice.entities.PaymentResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TestingController(
    @Value("toTicketCatalogue") val topic: String,
    @Autowired
    @Qualifier("toTicketCatalogueTemplate")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/test/toTicketCatalogue")
    fun post(@Validated @RequestBody paymentResponse: PaymentResponse): ResponseEntity<Any> {
        return try {
            log.info("Sending PaymentResponse to TicketCatalogueService")
            log.info("Sending message to Kafka {}", paymentResponse)
            val message: Message<PaymentResponse> = MessageBuilder
                .withPayload(paymentResponse)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }

}
