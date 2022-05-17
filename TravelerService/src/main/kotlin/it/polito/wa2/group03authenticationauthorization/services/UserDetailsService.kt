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
        val currentProfile = getProfile(profile.userId!!).orElseThrow()
        // UserID, Role, and Tickets cannot be changed with this method
        val newProfile = UserDetailsDTO(
            currentProfile.userId,
            profile.name,
            currentProfile.role,
            profile.address,
            profile.dateOfBirth,
            profile.telephoneNumber,
            currentProfile.tickets
        )
        val entity = UserDetails(
            newProfile.userId!!,
            newProfile.name!!,
            newProfile.role!!,
            newProfile.address,
            newProfile.dateOfBirth,
            newProfile.telephoneNumber
        )
        return userDetailsRepository.save(entity).toDTO()
    }

    fun getRegisteredUsernames(): List<String> {
        return userDetailsRepository.findAll().map { it.name }
    }
}