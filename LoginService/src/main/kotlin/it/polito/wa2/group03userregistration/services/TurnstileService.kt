package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.*
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.UserRole
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class TurnstileService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun register(turnstile: UserDTO) {
        // From DTO to entity
        val t = User(turnstile.username, turnstile.password, null, UserRole.TURNSTILE, 1)

        // Save the data in the DB
        t.salt = BCrypt.gensalt(10)
        t.password = BCrypt.hashpw(t.password, t.salt)
        userRepository.save(t)
    }
}
