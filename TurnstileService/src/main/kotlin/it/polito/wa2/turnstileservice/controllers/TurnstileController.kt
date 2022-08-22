package it.polito.wa2.turnstileservice.controllers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.turnstileservice.dto.*
import it.polito.wa2.turnstileservice.security.UserPrincipal
import it.polito.wa2.turnstileservice.services.TurnstileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


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
        } else {
            /**
             * TODO: Generate message with Kafka to TransitService
             *
             *  The user which did the transit --> In TransitDataDTO
             *  Which ticket they used --> In TransitDataDTO
             *  When --> Current clock
             *  Where, so which turnstile --> Logged in turnstile (val principal)
             */
        }

        // Kafka message sent correctly
        return ResponseEntity(transitData, HttpStatus.OK)
    }

    @Autowired
    lateinit var authenticationManager: ReactiveAuthenticationManager

    @Value("\${jwt.key}")
    lateinit var secret: String

    @PostMapping("/login")
    suspend fun login(@RequestBody loginData: TurnstileInDTO): ResponseEntity<LoginResponse> {
        val token = UsernamePasswordAuthenticationToken(
            loginData.username,
            loginData.password
        )
        val ret = authenticationManager.authenticate(token)
            .doOnError { ResponseEntity(null, HttpStatus.NOT_FOUND) }
            .flatMap { authentication ->
                val principal = authentication.principal as UserPrincipal
                val roles: List<String> = principal.authorities.map { it.authority.toString() }
                val jwtToken = Jwts.builder()
                    .setSubject(authentication.principal.toString())
                    .claim("roles", roles)
                    .setIssuedAt(Date(System.currentTimeMillis()))
                    .signWith(Keys.hmacShaKeyFor(secret.toByteArray()), SignatureAlgorithm.HS256)
                    .compact()
                // TODO add to response
                // val headers = mapOf("Authorization" to  "Bearer $token")
                Mono.just(ResponseEntity(LoginResponse(principal.username, jwtToken), HttpStatus.OK))
            }
        return ret.block()!!
    }
}