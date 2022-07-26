package it.polito.wa2.group03userregistration.enums

enum class UserRole {
    CUSTOMER, ADMIN
}

val UserRolesMapping = mapOf(
    UserRole.CUSTOMER to "CUSTOMER",
    UserRole.ADMIN to "ADMIN"
)
