package it.polito.wa2.turnstileservice.controllers

import it.polito.wa2.turnstileservice.dto.*
import it.polito.wa2.turnstileservice.services.TurnstileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId


@RestController
class TurnstileController(@Value("\${transit-service-endpoint}") transitServiceEndpoint: String) {

    @Autowired
    lateinit var turnstileService: TurnstileService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as Long }


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
    suspend fun generateTransit(@RequestBody transitData: TransitDataDTO): ResponseEntity<TransitData> {
        val startValidity = LocalDateTime.ofInstant(transitData.sta.toInstant(), ZoneId.systemDefault())
        val endValidity = LocalDateTime.ofInstant(transitData.exp.toInstant(), ZoneId.systemDefault())

        if (startValidity.isAfter(LocalDateTime.now())) {
            val err = TransitDataErrorDTO("The ticket is not yet valid!", transitData)
            return ResponseEntity(err, HttpStatus.BAD_REQUEST)
        } else if (endValidity.isBefore(LocalDateTime.now())) {
            val err = TransitDataErrorDTO("The ticket is expired!", transitData)
            return ResponseEntity(err, HttpStatus.BAD_REQUEST)
        }

        // TODO: Check ticket signature

        /**
         * TODO: Generate message with Kafka to TransitService
         *
         *  The user which did the transit --> In TransitDataDTO
         *  Which ticket they used --> In TransitDataDTO
         *  When --> Current clock
         *  Where, so which turnstile --> Logged in turnstile (val principal)
         */

        // Kafka message sent correctly
        return ResponseEntity(transitData, HttpStatus.OK)
    }
}
