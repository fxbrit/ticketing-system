package it.polito.wa2.group03authenticationauthorization.services

import it.polito.wa2.group03authenticationauthorization.dtos.UserDetailsDTO
import it.polito.wa2.group03authenticationauthorization.dtos.toDTO
import it.polito.wa2.group03authenticationauthorization.entities.UserDetails
import it.polito.wa2.group03authenticationauthorization.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserDetailsService {

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    fun getProfile(userId: Long): Optional<UserDetailsDTO> {
        return userDetailsRepository.findById(userId)
            .let { if (it.isPresent) Optional.of(it.get().toDTO()) else Optional.empty() }
    }

    fun updateProfile(profile: UserDetailsDTO): UserDetailsDTO {

        val e = userDetailsRepository.findById(profile.userId!!)

        if (e.isPresent) {
            val entity = e.get()
            entity.name = profile.name!!
            entity.address = profile.address
            entity.dateOfBirth = profile.dateOfBirth
            entity.telephoneNumber = profile.telephoneNumber

            return userDetailsRepository.save(entity).toDTO()
        } else {
            val entity = UserDetails(
                profile.userId!!,
                profile.name!!,
                profile.role!!,
                profile.address,
                profile.dateOfBirth,
                profile.telephoneNumber
            )
            return userDetailsRepository.save(entity).toDTO()
        }
    }

    fun getRegisteredUsernames(): List<String> {
        return userDetailsRepository.findAll().map { it.name }
    }
}