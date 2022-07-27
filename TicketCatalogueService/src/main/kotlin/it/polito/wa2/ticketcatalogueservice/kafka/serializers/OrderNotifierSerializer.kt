package it.polito.wa2.ticketcatalogueservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer
import it.polito.wa2.ticketcatalogueservice.entities.OrderNotifier
import org.apache.kafka.common.errors.SerializationException
import org.slf4j.LoggerFactory

class OrderNotifierSerializer : Serializer<OrderNotifier> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: OrderNotifier?): ByteArray? {
        log.info("Serializing OrderNotifier...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing OrderNotifier to ByteArray[]")
        )
    }

    override fun close() {}

}
