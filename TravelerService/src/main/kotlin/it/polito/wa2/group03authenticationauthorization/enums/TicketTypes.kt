package it.polito.wa2.group03authenticationauthorization.enums

enum class TicketTypes {
    ONE_WAY,
    WEEKEND,
    MONTHLY
}

val TicketTypeString = mapOf(
    TicketTypes.ONE_WAY to "one way",
    TicketTypes.WEEKEND to "weekend",
    TicketTypes.MONTHLY to "monthly"
)