package it.polito.wa2.turnstileservice.kafka.producers

import it.polito.wa2.turnstileservice.entities.TransitNotifier
import it.polito.wa2.turnstileservice.kafka.serializers.TransitNotifierSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class TransitNotifierProducerConfig(@Value("\${spring.kafka.bootstrap-servers}") private val server: String) {

    @Bean
    fun transitNotifierProducerFactory(): ProducerFactory<String, TransitNotifier> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TransitNotifierSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun transitNotifierTemplate(): KafkaTemplate<String, TransitNotifier> {
        return KafkaTemplate(transitNotifierProducerFactory())
    }

}
