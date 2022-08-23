package it.polito.wa2.turnstileservice.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.wa2.turnstileservice.dto.TurnstileInDTO
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class JSONAuthenticationConverter() : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val request = exchange.request
        val contentType = request.headers.contentType


        return if (contentType?.isCompatibleWith(MediaType.APPLICATION_JSON) == true) {
            request.body.next().flatMap { buffer ->
                val mapper = jacksonObjectMapper()
                Mono.just(mapper.readValue<TurnstileInDTO>(buffer.asInputStream()))
            }.map { r -> UsernamePasswordAuthenticationToken(r.username, r.password, ArrayList()) }
        } else {
            Mono.empty()
        }
    }
}