package it.polito.wa2.paymentservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.paymentservice.entities.PaymentBankResponse
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class PaymentBankResponseDeserializer : Deserializer<PaymentBankResponse> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): PaymentBankResponse? {
        log.info("Deserializing PaymentBankResponse...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to PaymentResponse"),
                Charsets.UTF_8
            ), PaymentBankResponse::class.java
        )
    }

    override fun close() {}

}
