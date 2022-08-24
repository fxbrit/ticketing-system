package it.polito.wa2.transitservice.kafka.consumers

import it.polito.wa2.transitservice.entities.Transit
import it.polito.wa2.transitservice.entities.TransitNotifier
import it.polito.wa2.transitservice.kafka.Topics
import it.polito.wa2.transitservice.repositories.TransitRepository
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TransitNotifierConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var transitRepository: TransitRepository

    @KafkaListener(
        containerFactory = "transitNotifierListenerContainerFactory",
        topics = [Topics.turnstileToTransit],
        groupId = "ctl"
    )
    fun listenFromTurnstileService(consumerRecord: ConsumerRecord<Any, TransitNotifier>) {

        logger.info("Incoming transit notifier {}", consumerRecord)

        val response = consumerRecord.value()
        runBlocking {
            transitRepository.save(
                Transit(
                    null,
                    response.ticketID.toString(),
                    response.userID,
                    response.turnstileID,
                    response.time
                )
            )
        }

    }

}
