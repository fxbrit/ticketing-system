package it.polito.wa2.ticketcatalogueservice.config

import io.r2dbc.spi.ConnectionFactory
import it.polito.wa2.ticketcatalogueservice.converters.OrderReadConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions


@Configuration
class R2DBCConfig: AbstractR2dbcConfiguration() {
    @Autowired
    lateinit var connectionFactory: ConnectionFactory
    override fun connectionFactory(): ConnectionFactory {
        return connectionFactory
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converterList: MutableList<Converter<*, *>?> = ArrayList<Converter<*, *>?>()
        converterList.add(OrderReadConverter())
        return R2dbcCustomConversions(storeConversions, converterList)
    }


}