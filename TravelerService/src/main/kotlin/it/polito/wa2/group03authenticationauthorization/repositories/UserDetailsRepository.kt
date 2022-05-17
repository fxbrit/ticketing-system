package it.polito.wa2.group03authenticationauthorization.repositories

import it.polito.wa2.group03authenticationauthorization.entities.UserDetails
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository : CrudRepository<UserDetails, Long>
