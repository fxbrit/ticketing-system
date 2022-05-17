package it.polito.wa2.group03authenticationauthorization.dtos

data class TicketUserActionDTO(
        val cmd: String,
        val quantity: Int,
        val zones: String,
        var userId: Long?
)