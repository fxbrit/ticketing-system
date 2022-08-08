package it.polito.wa2.group03userregistration.services

import it.polito.wa2.group03userregistration.dtos.AdministratorDTO
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.UserRole
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class AdministratorService {

    val passwordRegex =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$")
    val emailRegex =
        Regex("^[A-Za-z\\d+_.-]+(@)([A-Za-z\\d+_.-]+)(\\.)([A-Za-z\\d]+)\$")

    lateinit var userRepository: UserRepository

    fun enrollAdministrator(toRegister: AdministratorDTO): UserValidationStatus {

        /**
         * expect the role as a String and convert it to an enum.
         * in the validity checks we then compare enum names as we
         * expect either ADMIN or SUPERADMIN as valid roles for this
         * method.
         */
        val administrator = User(
            toRegister.username,
            toRegister.password,
            toRegister.email
        )

        val validity = isValid(administrator)

        if (validity == UserValidationStatus.VALID) {
            administrator.salt = BCrypt.gensalt(10)
            administrator.password = BCrypt.hashpw(administrator.password, administrator.salt)
            administrator.role = enumValueOf(toRegister.role)
            administrator.enabled = 1
            userRepository.save(administrator)
        }

        return validity

    }

    fun isValid(administrator: User): UserValidationStatus {

        return when {
            administrator.username.isBlank() -> UserValidationStatus.NO_USERNAME
            administrator.email.isBlank() -> UserValidationStatus.NO_EMAIL
            administrator.password.isNullOrBlank() -> UserValidationStatus.NO_PASSWORD
            !administrator.password!!.matches(passwordRegex) -> UserValidationStatus.WEAK_PASSWORD
            !administrator.email.matches(emailRegex) -> UserValidationStatus.INVALID_EMAIL
            administrator.role.name == UserRole.ADMIN.name || administrator.role.name == UserRole.SUPERADMIN.name -> UserValidationStatus.INVALID_ROLE
            userRepository.findByEmail(administrator.email) != null -> UserValidationStatus.EMAIL_ALREADY_EXISTS
            userRepository.findByUsername(administrator.username) != null -> UserValidationStatus.USERNAME_ALREADY_EXISTS
            else -> UserValidationStatus.VALID
        }

    }

}
