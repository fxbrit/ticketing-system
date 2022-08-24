package it.polito.wa2.turnstileservice.controllers

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SignatureException
import it.polito.wa2.turnstileservice.dto.*
import it.polito.wa2.turnstileservice.services.TurnstileService
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@RestController
class TurnstileController(
    @Value("\${transit-service-endpoint}") private val transitServiceEndpoint: String,
    @Value("\${jwt.ticket-signature-key}") private val ticketSignatureKey: String
) {

    @Autowired
    lateinit var turnstileService: TurnstileService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as Long }

    /** init the parser once with the controller, not on each call */
    private val jwtParser = Jwts.parserBuilder()
        .setSigningKey(Base64.getEncoder().encodeToString(ticketSignatureKey.toByteArray()))
        .build()

    @PostMapping("/register")
    suspend fun registerTurnstile(@RequestBody turnstileData: TurnstileInDTO): ResponseEntity<TurnstileOutDTO> {
        val res = turnstileService.register(turnstileData)
        return if (res is TurnstileOutInvalidDTO) {
            ResponseEntity(res, HttpStatus.BAD_REQUEST)
        } else {
            ResponseEntity(res, HttpStatus.CREATED)
        }
    }

    @PostMapping("/generateTransit")
    suspend fun generateTransit(@RequestBody jws: String): ResponseEntity<TransitDataDTO> {

        // Validate ticket signature (a turnstile should validate it anyway before calling this endpoint)
        val jwsBody: Claims
        try {
            jwsBody = jwtParser.parseClaimsJws(jws).body
        } catch (se: SignatureException) {
            val err = TransitDataErrorDTO("Wrong ticket signature! ${se.message}")
            return ResponseEntity(err, HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            val err = TransitDataErrorDTO("Error during ticket validation: ${e.message}")
            return ResponseEntity(err, HttpStatus.BAD_REQUEST)
        }

        /** validate start and end validity date for the ticket */
        val startValidity = LocalDateTime.ofInstant(
            Date((jwsBody["sta"] as Number).toLong()).toInstant(),
            ZoneId.systemDefault()
        )
        val endValidity = LocalDateTime.ofInstant(
            Date((jwsBody["exp"] as Number).toLong()).toInstant(),
            ZoneId.systemDefault()
        )
        if (startValidity.isAfter(LocalDateTime.now())) {
            val err = TransitDataErrorDTO("The ticket is not yet valid!")
            return ResponseEntity(err, HttpStatus.BAD_REQUEST)
        } else if (endValidity.isBefore(LocalDateTime.now())) {
            val err = TransitDataErrorDTO("The ticket is expired!")
            return ResponseEntity(err, HttpStatus.BAD_REQUEST)
        }

        /** if both checks are passed we register a transit using Kafka */
        val res = TransitDataValidDTO(
            UUID.fromString(jwsBody["sub"] as String),
            Date((jwsBody["iat"] as Number).toLong()),
            Date((jwsBody["sta"] as Number).toLong()),
            Date((jwsBody["exp"] as Number).toLong()),
            jwsBody["zid"] as String,
            (jwsBody["uid"] as Number).toLong(),
            jws,
            principal.awaitSingle()
        )

        turnstileService.sendMessage(res.userID, res.ticketID, LocalDateTime.now(), res.turnstileID)

        return ResponseEntity(res, HttpStatus.OK)

    }

    @GetMapping("/ticketSignatureKey")
    suspend fun getTicketSignatureKey(): String {
        return ticketSignatureKey
    }

}
