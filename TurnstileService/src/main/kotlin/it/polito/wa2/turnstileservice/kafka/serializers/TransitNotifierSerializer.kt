package it.polito.wa2.turnstileservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.turnstileservice.entities.TransitNotifier
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory

class TransitNotifierSerializer : Serializer<TransitNotifier> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: TransitNotifier?): ByteArray? {
        log.info("Serializing TransitNotifier...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing TransitNotifier to ByteArray[]")
        )
    }

    override fun close() {}

}
