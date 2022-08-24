package it.polito.wa2.transitservice.kafka.consumers

import it.polito.wa2.transitservice.entities.TransitNotifier
import it.polito.wa2.transitservice.kafka.serializers.TransitNotifierDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@EnableKafka
@Configuration
class TransitNotifierConsumerConfig(@Value("\${spring.kafka.bootstrap-servers}") private val server: String) {

    @Bean
    fun transitNotifierConsumerFactory(): ConsumerFactory<String?, TransitNotifier> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        props[ConsumerConfig.GROUP_ID_CONFIG] = "ctl"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = TransitNotifierDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun transitNotifierListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, TransitNotifier> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, TransitNotifier>()
        factory.consumerFactory = transitNotifierConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        factory.containerProperties.isSyncCommits = true
        return factory
    }

}
