package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.*
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.ActivationStatus
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.repositories.ActivationRepository
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class UserService {
    val passwordRegex =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$")
    val emailRegex =
        Regex("^[A-Za-z\\d+_.-]+(@)([A-Za-z\\d+_.-]+)(\\.)([A-Za-z\\d]+)\$")

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    fun registerUser(userToRegister: UserDTO): RegisterDTO {
        // From DTO to entity
        val user = User(userToRegister.username, userToRegister.password, userToRegister.email)

        val validationStatus = isValidUser(user)
        if (validationStatus != UserValidationStatus.VALID)
            return RegisterDTO(validationStatus, null)

        // If the user is valid then save their data in the DB
        // However the registration is not complete until activation with code
        user.salt = BCrypt.gensalt(10)
        user.password = BCrypt.hashpw(user.password, user.salt)
        val savedUser = userRepository.save(user)
        val activationDTO = emailService.insertActivation(savedUser)

        return RegisterDTO(validationStatus, activationDTO)
    }

    fun isValidUser(user: User): UserValidationStatus {
        return when {
            user.username.isBlank() -> UserValidationStatus.NO_USERNAME
            user.email.isBlank() -> UserValidationStatus.NO_EMAIL
            user.password.isBlank() -> UserValidationStatus.NO_PASSWORD
            !user.password.matches(passwordRegex) -> UserValidationStatus.WEAK_PASSWORD
            !user.email.matches(emailRegex) -> UserValidationStatus.INVALID_EMAIL
            userRepository.findByEmail(user.email) != null -> UserValidationStatus.EMAIL_ALREADY_EXISTS
            userRepository.findByUsername(user.username) != null -> UserValidationStatus.USERNAME_ALREADY_EXISTS
            else -> UserValidationStatus.VALID
        }
    }

    fun validateUser(activation: ActivationDTO): ValidateDTO {
        val savedActivation = activationRepository.findById(activation.provisionalId).orElse(null)

        // Provisional id does not exist
        savedActivation == null &&
                return ValidateDTO(ActivationStatus.ID_DOES_NOT_EXIST, null)

        // Validation time expired
        if (savedActivation.expirationDate!!.before(Date.from(Instant.now()))) {
            activationRepository.delete(savedActivation)
            return ValidateDTO(ActivationStatus.EXPIRED, null)
        }

        // Wrong activation code or code has not been issued to this email
        if (activation.activationCode != savedActivation.activationCode || activation.email != savedActivation.email) {
            savedActivation.attempt--

            if (savedActivation.attempt == 0) {
                activationRepository.delete(savedActivation)
                userRepository.delete(savedActivation.userActivation)
            } else {
                activationRepository.save(savedActivation)
            }
            return ValidateDTO(ActivationStatus.WRONG_ACTIVATION_CODE, null)
        }

        activationRepository.deleteById(activation.provisionalId)
        userRepository.enableUserByEmail(savedActivation.userActivation.email)
        return ValidateDTO(ActivationStatus.SUCCESSFUL, savedActivation.userActivation.toDTO())
    }

}
