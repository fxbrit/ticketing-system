package it.polito.wa2.transitservice.dto

import it.polito.wa2.transitservice.entities.TransitsPerTurnstile

data class TransitsPerTurnstileDTO(
    val turnstileID: Long,
    val numberOfTransits: Long
)

fun TransitsPerTurnstile.toDTO() = TransitsPerTurnstileDTO(turnstileID, numberOfTransits)
