package it.polito.wa2.group03userregistration.integration

import com.dumbster.smtp.SimpleSmtpServer
import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.dtos.AdministratorDTO
import it.polito.wa2.group03userregistration.dtos.UserDTO
import it.polito.wa2.group03userregistration.utils.LoginDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JsonParserFactory
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*


// This attribute recreates the WebServer before each test, this is a simple-yet-ugly
// workaround to re-initialize the already checked ticket list before each test
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WebControllerTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        val dumbster: SimpleSmtpServer = SimpleSmtpServer.start(SimpleSmtpServer.AUTO_SMTP_PORT)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.mail.port", dumbster::getPort)
            registry.add("spring.mail.host") { "localhost" }
        }
    }

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun clearMails() {
        dumbster.reset()
    }

    @Test
    fun `Normal request flow`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "Asd123!asdasd", "me@email.com"))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        Assertions.assertEquals(1, dumbster.receivedEmails.size)
    }

    @Test
    fun `Wrong email format`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "wrong-email.it", "Asd123!asdasd"))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Weak password`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "me@email.com", "123"))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Missing username field`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "", "Password1234!", "me@email.com"))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Missing email field`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "Password123!", ""))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Missing password field`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "", "me@email.com"))

        // Act
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(0, dumbster.receivedEmails.size)
    }

    @Test
    fun `Validation correct and login`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "Password1234!", "me@email.com"))
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )
        val parser = JsonParserFactory.getJsonParser()
        val provisionalId = parser.parseMap(response.body)["provisional_id"]
        val email = parser.parseMap(response.body)["email"]
        val activationCode = dumbster.receivedEmails[0].body.split("code:")[1].split(" ")[0].trim()

        // Act
        val activationRequest =
            HttpEntity(ActivationDTO(UUID.fromString(provisionalId as String), email as String, activationCode))
        val activationResponse = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            activationRequest
        )

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, activationResponse.statusCode)

        // a bunch of wrong login attempts
        val wrongPswLoginRequest = HttpEntity(LoginDTO("testuser", "WrongPassword1234!"))
        val wrongUsernameLoginRequest = HttpEntity(LoginDTO("testuserWrong", "Password1234!"))
        val wrongPswLoginResponse = restTemplate.postForEntity<String>(
            "$baseUrl/user/login",
            wrongPswLoginRequest
        )
        val wrongUsernameLoginResponse = restTemplate.postForEntity<String>(
            "$baseUrl/user/login",
            wrongUsernameLoginRequest
        )
        Assertions.assertEquals(HttpStatus.NOT_FOUND, wrongPswLoginResponse.statusCode)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, wrongUsernameLoginResponse.statusCode)

        // a successful login attempt
        val loginRequest = HttpEntity(LoginDTO("testuser", "Password1234!"))
        val loginResponse = restTemplate.postForEntity<String>(
            "$baseUrl/user/login",
            loginRequest
        )
        val token: String = try {
            parser.parseMap(loginResponse.body)["token"].toString()
        } catch (e: Exception) {
            ""
        }
        Assertions.assertNotEquals("", token)

    }

    @Test
    fun `Validation wrong code`() {

        // Arrange
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO(null, "testuser", "Password1234!", "me@email.com"))
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )
        val parser = JsonParserFactory.getJsonParser()
        val provisionalId = parser.parseMap(response.body)["provisional_id"]
        val email = parser.parseMap(response.body)["email"]
        val activationCode = "fake code"

        // Act
        val activationRequest =
            HttpEntity(ActivationDTO(UUID.fromString(provisionalId as String), email as String, activationCode))
        val activationResponse = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            activationRequest
        )

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, activationResponse.statusCode)
    }

    @Test
    fun enrollAdmin() {

        val baseUrl = "http://localhost:$port"
        val parser = JsonParserFactory.getJsonParser()
        val headers = HttpHeaders()

        /**
         * we are making authenticated requests using a JWS that corresponds to:
         * {"sub": "1", "roles": ["SUPERADMIN"], "iat": 1661879054, "exp": 1977498309}
         */
        val token =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiU1VQRVJBRE1JTiJdLCJpYXQiOjE2NjE4NzkwNTQsImV4cCI6MTk3NzQ5ODMwOX0.YfsTaqycNzUNYAJnyehqaoO_MxvJRInkQiFjLO2aCMo"
        headers.set("Authorization", "Bearer $token")

        /**
         * registering an admin, httpie equivalent would be:
         * username=giuliano password=PSW111aaa! email=giuliano@adminmail.com role="ADMIN"
         */
        val adminRegister =
            HttpEntity(
                AdministratorDTO(
                    null,
                    "giuliano",
                    "giuliano@adminmail.com",
                    "PSW111aaa!",
                    "ADMIN"
                ),
                headers
            )
        val adminRegisterResponse = restTemplate.postForEntity<String>(
            "$baseUrl/admin/register",
            adminRegister
        )
        Assertions.assertEquals(HttpStatus.ACCEPTED, adminRegisterResponse.statusCode)

        /**
         * registering a superadmin, httpie equivalent would be:
         * username=marco password=PSW111aaa! email=marco@adminmail.com role="SUPERADMIN"
         */
        val superadminRegister =
            HttpEntity(
                AdministratorDTO(
                    null,
                    "marco",
                    "marco@adminmail.com",
                    "PSW111aaa!",
                    "SUPERADMIN"
                ),
                headers
            )
        val superadminRegisterResponse = restTemplate.postForEntity<String>(
            "$baseUrl/admin/register",
            superadminRegister
        )
        Assertions.assertEquals(HttpStatus.ACCEPTED, superadminRegisterResponse.statusCode)

        /**
         * now we login with the newly registered admin and we try to enroll
         * another admin, which causes an error because the path is protected
         * so that only superadmins can enroll others
         */
        val loginAdmin = HttpEntity(LoginDTO("giuliano", "PSW111aaa!"))
        val loginAdminResponse = restTemplate.postForEntity<String>(
            "$baseUrl/user/login",
            loginAdmin
        )
        val adminToken: String = try {
            parser.parseMap(loginAdminResponse.body)["token"].toString()
        } catch (e: Exception) {
            ""
        }
        headers.set("Authorization", "Bearer $adminToken")
        val adminRegisterFail =
            HttpEntity(
                AdministratorDTO(
                    null,
                    "luca",
                    "luca@adminmail.com",
                    "PSW111aaa!",
                    "ADMIN"
                ),
                headers
            )
        val adminRegisterFailResponse = restTemplate.postForEntity<String>(
            "$baseUrl/admin/register",
            adminRegisterFail
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, adminRegisterFailResponse.statusCode)

    }

}
