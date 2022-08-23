package it.polito.wa2.turnstileservice.security

import it.polito.wa2.turnstileservice.repositories.TurnstileRepository
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomUserDetailsService: ReactiveUserDetailsService {

    @Autowired
    lateinit var turnstileRepository: TurnstileRepository

    override fun findByUsername(username: String): Mono<UserDetails> {
            val t = runBlocking { turnstileRepository.findTurnstileByUsername(username) }
            return if (t != null) {
                Mono.just(UserPrincipal(t))
            } else {
                Mono.empty()
            }
        }
    }
