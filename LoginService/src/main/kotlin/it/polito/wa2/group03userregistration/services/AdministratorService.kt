package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.AdministratorDTO
import it.polito.wa2.group03userregistration.entities.Administrator
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.repositories.AdministratorRepository
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class AdministratorService {

    val passwordRegex =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$")
    val emailRegex =
        Regex("^[A-Za-z\\d+_.-]+(@)([A-Za-z\\d+_.-]+)(\\.)([A-Za-z\\d]+)\$")

    lateinit var administratorRepository: AdministratorRepository

    fun enrollAdministrator(toRegister: AdministratorDTO): UserValidationStatus {

        val administrator = Administrator(
            toRegister.username,
            toRegister.password,
            toRegister.email,
            toRegister.enroll
        )

        val validity = isValid(administrator)

        if (validity == UserValidationStatus.VALID) {
            administrator.salt = BCrypt.gensalt(10)
            administrator.password = BCrypt.hashpw(administrator.password, administrator.salt)
            administratorRepository.save(administrator)
        }

        return validity

    }

    fun isValid(administrator: Administrator): UserValidationStatus {

        return when {
            administrator.username.isBlank() -> UserValidationStatus.NO_USERNAME
            administrator.email.isBlank() -> UserValidationStatus.NO_EMAIL
            administrator.password.isNullOrBlank() -> UserValidationStatus.NO_PASSWORD
            !administrator.password!!.matches(passwordRegex) -> UserValidationStatus.WEAK_PASSWORD
            !administrator.email.matches(emailRegex) -> UserValidationStatus.INVALID_EMAIL
            administratorRepository.findByEmail(administrator.email) != null -> UserValidationStatus.EMAIL_ALREADY_EXISTS
            administratorRepository.findByUsername(administrator.username) != null -> UserValidationStatus.USERNAME_ALREADY_EXISTS
            else -> UserValidationStatus.VALID
        }

    }

}
