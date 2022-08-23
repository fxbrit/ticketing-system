package it.polito.wa2.turnstileservice.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.turnstileservice.dto.LoginResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono
import java.util.*

class AuthenticationResponseHandler(private val secret: String) : ServerAuthenticationSuccessHandler,
    ServerAuthenticationFailureHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val principal = authentication.principal as UserPrincipal
        val roles: List<String> = principal.authorities.map { it.authority.toString() }
        val jwtToken = Jwts.builder()
            .setSubject(principal.username)
            .claim("roles", roles)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray()), SignatureAlgorithm.HS256)
            .compact()

        val response = webFilterExchange.exchange.response
        val body = LoginResponse(principal.username, jwtToken)
        val buf = response.bufferFactory().wrap(jacksonObjectMapper().writeValueAsBytes(body))

        response.headers.setBearerAuth(jwtToken)
        response.headers.contentType = MediaType.APPLICATION_JSON
        response.statusCode = HttpStatus.OK
        return response.writeWith(Mono.just(buf))
    }

    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange,
        exception: AuthenticationException?
    ): Mono<Void> {
        val response = webFilterExchange.exchange.response
        val body = exception?.message ?: "Error"
        val buf = response.bufferFactory().wrap(jacksonObjectMapper().writeValueAsBytes(body))

        response.statusCode = HttpStatus.NOT_FOUND
        response.headers.contentType = MediaType.TEXT_PLAIN
        return response.writeWith(Mono.just(buf))
    }
}