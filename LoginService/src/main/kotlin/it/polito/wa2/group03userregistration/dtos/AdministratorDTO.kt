package it.polito.wa2.group03userregistration.dtos

data class AdministratorDTO(
    val administratorId: Long?,
    val username: String,
    val email: String,
    val password: String?,
    var role: String
)
