package it.polito.wa2.group03userregistration.dtos

import it.polito.wa2.group03userregistration.entities.Administrator

data class AdministratorDTO(
    val administratorId: Long?,
    val username: String,
    val email: String,
    val password: String?,
    val enroll: Int
)

fun Administrator.toDTO() = AdministratorDTO(id, username, email, password, enroll)
