package it.polito.wa2.paymentservice.controller

import it.polito.wa2.paymentservice.entities.PaymentRequest
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
    @Value("fromBank") val topicFB: String,
    @Value("fromTicketCatalogue") val topicFTC: String,
    @Autowired
    @Qualifier("toTicketCatalogueTemplate")
    private val ticketCatalogueTemplate: KafkaTemplate<String, Any>,
    @Autowired
    @Qualifier("toBankTemplate")
    private val bankTemplate: KafkaTemplate<String, Any>
) {

    private val log = LoggerFactory.getLogger(javaClass)


    /**
     * simulates the Bank publishing a response:
     * curl -H "Content-Type: application/json" -d '{"paymentId":10, "status":1}' localhost:8080/tests/reponseFromBank
     *
     * expected behavior:
     *  - message is deserialized
     *  - DB is updated with response status
     *  - message is forwarded to outgoing topic
     */
    @PostMapping("/tests/reponseFromBank")
    fun reponseFromBank(@Validated @RequestBody paymentResponse: PaymentResponse): ResponseEntity<Any> {
        return try {
            log.info("New message from the Bank")
            log.info("Bank sending message to Kafka that looks like: {}", paymentResponse)
            val message: Message<PaymentResponse> = MessageBuilder
                .withPayload(paymentResponse)
                .setHeader(KafkaHeaders.TOPIC, topicFB)
                .build()
            ticketCatalogueTemplate.send(message)
            log.info("Message from Bank sent with success")
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }

    /**
     * simulates the TicketCatalogueService publishing a request:
     * curl -H "Content-Type: application/json" \
     *      -d '{"paymentId":1, "userId":2, "creditCardNumber":333, "cvv":445, "expirationDate":"2022-05-25", "amount":10}' \
     *      localhost:8080/tests/requestTicketCatalogue
     *
     * expected behavior:
     *  - message is deserialized
     *  - (paymentid,userid,0) is saved to DB, to indicate the pending transaction
     *  - message is forwarded to outgoing topic, towards Bank
     */
    @PostMapping("/tests/requestTicketCatalogue")
    fun requestTicketCatalogue(@Validated @RequestBody paymentRequest: PaymentRequest): ResponseEntity<Any> {
        return try {
            log.info("New message from the TicketCatalogue")
            log.info("TicketCatalogue sending message to Kafka that looks like: {}", paymentRequest)
            val message: Message<PaymentRequest> = MessageBuilder
                .withPayload(paymentRequest)
                .setHeader(KafkaHeaders.TOPIC, topicFTC)
                .build()
            bankTemplate.send(message)
            log.info("Message from TicketCatalogue sent with success")
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error to send message")
        }
    }

}
