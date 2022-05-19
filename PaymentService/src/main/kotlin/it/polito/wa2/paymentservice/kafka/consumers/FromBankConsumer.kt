package it.polito.wa2.paymentservice.kafka.consumers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment

class FromBankConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["fromBank"], groupId = "ppr")
    fun listenFromBank(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received from Bank {}", consumerRecord)
        ack.acknowledge()
    }

}
