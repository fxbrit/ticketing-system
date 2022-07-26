package it.polito.wa2.group03userregistration.repositories

import it.polito.wa2.group03userregistration.entities.Administrator
import it.polito.wa2.group03userregistration.entities.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AdministratorRepository : CrudRepository<Administrator, Long> {

    @Query("SELECT u FROM Administrator u WHERE u.email = ?1")
    fun findByEmail(email: String): User?

    @Query("SELECT u FROM Administrator u WHERE u.username = ?1")
    fun findByUsername(username: String): User?

}
