package it.polito.wa2.group03userregistration.enums

enum class UserRole {
    CUSTOMER, ADMIN, SUPERADMIN, TURNSTILE
}

val UserRolesMapping = mapOf(
    UserRole.CUSTOMER to "CUSTOMER",
    UserRole.ADMIN to "ADMIN",
    UserRole.SUPERADMIN to "SUPERADMIN",
    UserRole.TURNSTILE to "TURNSTILE"
)
