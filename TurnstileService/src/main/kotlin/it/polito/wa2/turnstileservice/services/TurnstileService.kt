package it.polito.wa2.turnstileservice.services

import it.polito.wa2.turnstileservice.dto.TurnstileInDTO
import it.polito.wa2.turnstileservice.dto.TurnstileOutDTO
import it.polito.wa2.turnstileservice.dto.TurnstileOutInvalidDTO
import it.polito.wa2.turnstileservice.dto.toDTO
import it.polito.wa2.turnstileservice.entities.Turnstile
import it.polito.wa2.turnstileservice.repositories.TurnstileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class TurnstileService {

    @Autowired
    lateinit var turnstileRepository: TurnstileRepository

    suspend fun register(turnstile: TurnstileInDTO): TurnstileOutDTO {
        if (turnstileRepository.findTurnstileByUsername(turnstile.username) != null) {
            return TurnstileOutInvalidDTO("Username already exists")
        }

        // From DTO to entity
        val t = Turnstile(null, turnstile.username, turnstile.password, null)

        // Save the data in the DB
        t.salt = BCrypt.gensalt(10)
        t.password = BCrypt.hashpw(t.password, t.salt)
        return turnstileRepository.save(t).toDTO()
    }

}
