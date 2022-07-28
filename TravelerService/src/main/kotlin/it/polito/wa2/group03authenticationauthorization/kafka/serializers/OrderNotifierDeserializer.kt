package it.polito.wa2.group03authenticationauthorization.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group03authenticationauthorization.entities.OrderNotifier
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class OrderNotifierDeserializer : Deserializer<OrderNotifier> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): OrderNotifier? {
        log.info("Deserializing OrderNotifier...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to OrderNotifier"),
                Charsets.UTF_8
            ), OrderNotifier::class.java
        )
    }

    override fun close() {}

}