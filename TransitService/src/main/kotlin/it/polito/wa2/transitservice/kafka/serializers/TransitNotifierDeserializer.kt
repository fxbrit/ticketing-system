package it.polito.wa2.transitservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.transitservice.entities.TransitNotifier
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class TransitNotifierDeserializer : Deserializer<TransitNotifier> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): TransitNotifier? {
        log.info("Deserializing TransitNotifier...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to TransitNotifier"),
                Charsets.UTF_8
            ), TransitNotifier::class.java
        )
    }

    override fun close() {}

}
