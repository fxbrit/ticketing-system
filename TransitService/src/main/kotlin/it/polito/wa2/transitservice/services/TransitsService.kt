package it.polito.wa2.transitservice.services

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import it.polito.wa2.transitservice.dto.TransitsPerTurnstileDTO
import it.polito.wa2.transitservice.dto.toDTO
import it.polito.wa2.transitservice.entities.TransitsPerTurnstile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import java.util.function.BiFunction


@Bean
fun databaseClient(
    @Value("spring.r2dbc.url") url: String,
    @Value("spring.r2dbc.name") name: String,
    @Value("spring.r2dbc.username") username: String,
    @Value("spring.r2dbc.password") password: String
): DatabaseClient {
    val connectionFactory = PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host(url)
            .database(name)
            .username(username)
            .password(password)
            .build()
    )
    return DatabaseClient.builder()
        .connectionFactory(connectionFactory)
        .namedParameters(true)
        .build()
}

@Service
class TransitsService {

    @Autowired
    lateinit var databaseClient: DatabaseClient

    suspend fun transitsPerTurnstile(): Flow<TransitsPerTurnstileDTO> {
        val mappingFunction: BiFunction<Row, RowMetadata, TransitsPerTurnstileDTO> =
            BiFunction<Row, RowMetadata, TransitsPerTurnstileDTO> { row, _ ->
                TransitsPerTurnstile(
                    (row.get("turnstileid") as Number).toLong(),
                    (row.get("num_transits") as Number).toLong()
                ).toDTO()
            }

        return databaseClient
            .sql("select t.turnstileid, count(*) as num_transits from transits t group by t.turnstileid")
            .map(mappingFunction)
            .all().sort { o1, o2 -> o1.turnstileID.compareTo(o2.turnstileID) }.asFlow()
    }
}
