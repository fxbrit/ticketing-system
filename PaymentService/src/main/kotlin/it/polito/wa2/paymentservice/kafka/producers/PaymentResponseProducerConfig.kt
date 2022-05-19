package it.polito.wa2.paymentservice.kafka.producers

import it.polito.wa2.paymentservice.kafka.serializers.PaymentResponseSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class PaymentResponseProducerConfig(@Value("\${spring.kafka.bootstrap-servers}") private val server: String) {

    @Bean
    fun toTicketCatalogueProducerFactory(): ProducerFactory<String, Any> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = PaymentResponseSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun toTicketCatalogueTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(toTicketCatalogueProducerFactory())
    }

}
