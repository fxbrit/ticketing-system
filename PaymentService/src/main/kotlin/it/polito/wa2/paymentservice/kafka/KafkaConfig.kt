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

    /** message from PaymentService to Bank. */
    @Bean
    fun toBank(): NewTopic {
        return NewTopic("toBank", 1, 1.toShort())
    }

    /**
     * message from Bank to PaymentService.
     * PaymentService is the consumer -> FromBankConsumer.
     */
    @Bean
    fun fromBank(): NewTopic {
        return NewTopic("fromBank", 1, 1.toShort())
    }

    /** message from PaymentService to TicketCatalogueService. */
    @Bean
    fun toTicketCatalogue(): NewTopic {
        return NewTopic("toTicketCatalogue", 1, 1.toShort())
    }

    /**
     * message from TicketCatalogueService to PaymentService.
     * PaymentService is the consumer -> FromTicketCatalogueConsumer.
     */
    @Bean
    fun fromTicketCatalogue(): NewTopic {
        return NewTopic("fromTicketCatalogue", 1, 1.toShort())
    }

}
