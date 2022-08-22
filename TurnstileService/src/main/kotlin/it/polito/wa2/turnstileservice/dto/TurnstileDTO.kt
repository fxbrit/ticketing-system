package it.polito.wa2.turnstileservice.dto

import it.polito.wa2.turnstileservice.entities.Turnstile

data class TurnstileInDTO(
    val username: String,
    val password: String
)

interface TurnstileOutDTO

data class TurnstileOutValidDTO(
    val id: Long,
    val username: String
) : TurnstileOutDTO

data class TurnstileOutInvalidDTO(
    val errorMessage: String
) : TurnstileOutDTO

fun Turnstile.toDTO() = TurnstileOutValidDTO(id!!, username)