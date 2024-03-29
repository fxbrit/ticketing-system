package it.polito.wa2.group03userregistration.controllers

import it.polito.wa2.group03userregistration.dtos.ActivationDTO
import it.polito.wa2.group03userregistration.dtos.AdministratorDTO
import it.polito.wa2.group03userregistration.dtos.UserDTO
import it.polito.wa2.group03userregistration.enums.ActivationMessages
import it.polito.wa2.group03userregistration.enums.ActivationStatus
import it.polito.wa2.group03userregistration.enums.UserValidationMessages
import it.polito.wa2.group03userregistration.enums.UserValidationStatus
import it.polito.wa2.group03userregistration.services.AdministratorService
import it.polito.wa2.group03userregistration.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

// Data returned by POST /users/register
interface RegisterResponse
data class RegisterResponseOK(
    val provisional_id: UUID,
    val email: String
) : RegisterResponse

data class RegisterResponseError(
    val errorType: UserValidationStatus,
    val errorMessage: String?
) : RegisterResponse

// Data returned by POST /users/validate
interface ValidateResponse
data class ValidateResponseOK(
    val userId: Long,
    val username: String,
    val email: String
) : ValidateResponse

data class ValidateResponseError(
    val errorType: ActivationStatus,
    val errorMessage: String?
) : ValidateResponse

data class RegisterResponseOKAdmin(
    val status: UserValidationStatus,
    val username: String,
    val email: String
) : ValidateResponse

data class RegisterResponseErrorAdmin(
    val errorType: UserValidationStatus
) : ValidateResponse

@RestController
class WebController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var administratorService: AdministratorService

    @PostMapping("/user/register")
    fun registerUser(@RequestBody payload: UserDTO): ResponseEntity<RegisterResponse> {
        val registerDTO = userService.registerUser(payload)
        val resBody: RegisterResponse

        return if (registerDTO.status == UserValidationStatus.VALID) {
            resBody = RegisterResponseOK(registerDTO.activation!!.provisionalId, registerDTO.activation.email!!)
            ResponseEntity.status(HttpStatus.ACCEPTED).body(resBody)
        } else {
            resBody = RegisterResponseError(registerDTO.status, UserValidationMessages[registerDTO.status])
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody)
        }
    }

    @PostMapping("/user/validate")
    fun validateUser(@RequestBody payload: ActivationDTO): ResponseEntity<ValidateResponse> {
        val validateDTO = userService.validateUser(payload)
        val resBody: ValidateResponse

        return if (validateDTO.status == ActivationStatus.SUCCESSFUL) {
            resBody =
                ValidateResponseOK(validateDTO.user!!.userId!!, validateDTO.user.username, validateDTO.user.email)
            ResponseEntity.status(HttpStatus.CREATED).body(resBody)
        } else {
            resBody = ValidateResponseError(validateDTO.status, ActivationMessages[validateDTO.status])
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody)
        }
    }

    @PostMapping("/admin/register")
    fun registerAdministrator(@RequestBody payload: AdministratorDTO): ResponseEntity<ValidateResponse> {

        val result = administratorService.enrollAdministrator(payload)
        val resBody: ValidateResponse

        return if (result == UserValidationStatus.VALID) {
            resBody =
                RegisterResponseOKAdmin(result, payload.username, payload.email)
            ResponseEntity.status(HttpStatus.ACCEPTED).body(resBody)
        } else {
            resBody = RegisterResponseErrorAdmin(result)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody)
        }

    }

}
