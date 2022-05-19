package it.polito.wa2.paymentservice.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

/** ensures that spring finishing booting the topic is going to be created. */
@Configuration
class KafkaConfig(@Value("\${spring.kafka.bootstrap-servers}") private val server: String) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        return KafkaAdmin(configs)
    }

    /** outgoing payment request from PaymentService to Bank. */
    @Bean
    fun outRequests(): NewTopic {
        return NewTopic("outRequests", 1, 1.toShort())
    }

    /**
     * incoming payment responses from Bank to PaymentService.
     * PaymentService is the consumer here.
     */
    @Bean
    fun inResponses(): NewTopic {
        return NewTopic("inResponses", 1, 1.toShort())
    }

    /** outgoing payment responses from PaymentService to other internal services. */
    @Bean
    fun outResponses(): NewTopic {
        return NewTopic("outResponses", 1, 1.toShort())
    }

    /**
     * incoming payment requests from other internal services to PaymentService.
     * PaymentService is the consumer here.
     */
    @Bean
    fun fromTicketCatalogue(): NewTopic {
        return NewTopic("inRequests", 1, 1.toShort())
    }

}
