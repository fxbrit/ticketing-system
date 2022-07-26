package it.polito.wa2.group03userregistration.entities

import it.polito.wa2.group03userregistration.enums.UserRole
import javax.persistence.*

@Entity
class Administrator(var username: String, var password: String?, var email: String, var enroll: Boolean) {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null

    var salt: String = ""

    var role: UserRole = UserRole.ADMIN

    var enabled = 1

}
