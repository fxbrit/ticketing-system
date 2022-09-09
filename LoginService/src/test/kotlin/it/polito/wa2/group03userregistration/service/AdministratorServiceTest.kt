package it.polito.wa2.group03userregistration.service

import it.polito.wa2.group03userregistration.dtos.AdministratorDTO
import it.polito.wa2.group03userregistration.entities.User
import it.polito.wa2.group03userregistration.enums.UserRole
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.repositories.UserRepository
import it.polito.wa2.group03userregistration.services.AdministratorService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdministratorServiceTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var administratorService: AdministratorService

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun testIsValid() {

        val username = "testadmin1"
        val psw = "P4ssw0rd!"
        val email = "admintest1@maildomain.invalid"
        val usernameSuperadmin = "superadmin1"
        val emailSuperadmin = "superadmin1@maildomain.invalid"

        /** a valid admin and a valid superadmin */
        val validAdmin = User(username, psw, email)
        validAdmin.role = UserRole.ADMIN
        val validSuperadmin = User(usernameSuperadmin, psw, emailSuperadmin)
        validSuperadmin.role = UserRole.SUPERADMIN
        Assertions.assertEquals(UserValidationStatus.VALID, administratorService.isValid(validAdmin))
        Assertions.assertEquals(UserValidationStatus.VALID, administratorService.isValid(validSuperadmin))

        /** admin with blank username */
        val blankUsername = User("", psw, email)
        Assertions.assertEquals(UserValidationStatus.NO_USERNAME, administratorService.isValid(blankUsername))

        /** admin with blank email and broken email */
        val blankEmail = User(username, psw, "")
        val wrongEmail = User(username, psw, "wrong-looking-email")
        Assertions.assertEquals(UserValidationStatus.NO_EMAIL, administratorService.isValid(blankEmail))
        Assertions.assertEquals(UserValidationStatus.INVALID_EMAIL, administratorService.isValid(wrongEmail))

        /** admin with blank password and weak password */
        val blankPassword = User(username, "", email)
        val weakPassword = User(username, "ab12", email)
        Assertions.assertEquals(UserValidationStatus.NO_PASSWORD, administratorService.isValid(blankPassword))
        Assertions.assertEquals(UserValidationStatus.WEAK_PASSWORD, administratorService.isValid(weakPassword))

        /** admin with CUSTOMER role **/
        validSuperadmin.role = UserRole.CUSTOMER
        Assertions.assertEquals(UserValidationStatus.INVALID_ROLE, administratorService.isValid(validSuperadmin))

        /** admin with an existing email and then admin with an existing username */
        userRepository.save(validAdmin)
        val duplicateUsername = User(username, psw, "anotheradmin@maildomain.invalid")
        duplicateUsername.role = UserRole.ADMIN
        val duplicateEmail = User("another_admin", psw, email)
        duplicateEmail.role = UserRole.ADMIN
        Assertions.assertEquals(
            UserValidationStatus.USERNAME_ALREADY_EXISTS,
            administratorService.isValid(duplicateUsername)
        )
        Assertions.assertEquals(UserValidationStatus.EMAIL_ALREADY_EXISTS, administratorService.isValid(duplicateEmail))

    }

    @Test
    fun enrollAdmin() {

        val usernameAdmin = "admin3"
        val usernameSuperadmin = "superadmin3"
        val psw = "str0nG_123!"
        val emailAdmin = "admin3@maildomain.invalid"
        val emailSuperadmin = "superadmin3@maildomain.invalid"

        /**
         * we only test the successful case for admin and superadmin, and one generic fail
         * as all the other cases have been covered in previous tests.
         */
        val admin = AdministratorDTO(null, usernameAdmin, emailAdmin, psw, UserRole.ADMIN.name)
        val superadmin = AdministratorDTO(null, usernameSuperadmin, emailSuperadmin, psw, UserRole.SUPERADMIN.name)
        val invalidAdmin = AdministratorDTO(null, "", "", psw, "")

        Assertions.assertEquals(
            UserValidationStatus.VALID,
            administratorService.enrollAdministrator(admin)
        )
        Assertions.assertEquals(
            UserValidationStatus.VALID,
            administratorService.enrollAdministrator(superadmin)
        )
        Assertions.assertNotEquals(UserValidationStatus.VALID, administratorService.enrollAdministrator(invalidAdmin))

    }

}
