package it.polito.wa2.group03authenticationauthorization.services

import it.polito.wa2.group03authenticationauthorization.dtos.TicketUserActionDTO
import it.polito.wa2.group03authenticationauthorization.dtos.toDTO
import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import it.polito.wa2.group03authenticationauthorization.entities.UserDetails
import it.polito.wa2.group03authenticationauthorization.repositories.UserDetailsRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketsServiceTests {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketsService: TicketsService

    @Autowired
    @Value("\${jwt.outgoing-key}")
    lateinit var key: String

    @Test
    fun integration() {

        val userDetails = UserDetails(
            1,
            "Mario",
            "[ADMIN]",
            null,
            null,
            null
        )
        val user = userDetailsRepository.save(userDetails)

        val dto = TicketUserActionDTO(
            "",
            "one way",
            2,
            "1",
            userDetails.userId,
        )
        val added = ticketsService.createTickets(dto)
        val tickets = ticketsService.getTickets(userDetails.userId)

        Assertions.assertEquals(added.size, tickets.size)
        Assertions.assertEquals(tickets[0].uid, userDetails.userId)

        val ticket = TicketPurchased(
            user,
            tickets[0].sta,
            tickets[0].exp,
            tickets[0].zid
        )
        ticket.ticketId = tickets[0].sub
        ticket.issuedAt = tickets[0].iat!!
        val jws = ticket.toDTO(key).jws
        Assertions.assertEquals(tickets[0].jws, jws)

    }


}