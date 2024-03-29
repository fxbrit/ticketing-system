package it.polito.wa2.group03authenticationauthorization.controllers

import io.github.g0dkar.qrcode.QRCode
import it.polito.wa2.group03authenticationauthorization.dtos.TicketPurchasedDTO
import it.polito.wa2.group03authenticationauthorization.dtos.UserDetailsDTO
import it.polito.wa2.group03authenticationauthorization.services.TicketsService
import it.polito.wa2.group03authenticationauthorization.services.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.util.UUID

interface TicketResponse
data class TicketResponseOk(
    val jws: String?
) : TicketResponse

@RestController
class TicketsController {

    @Autowired
    lateinit var ticketsService: TicketsService

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @GetMapping("/my/profile")
    fun getMyProfile(): ResponseEntity<UserDetailsDTO> {
        val authorizedUser = SecurityContextHolder.getContext().authentication
        val user = userDetailsService.getProfile(authorizedUser.principal.toString().toLong())
        return if (user.isPresent)
            ResponseEntity.status(HttpStatus.OK).body(user.get())
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @PutMapping("/my/profile")
    fun updateMyProfile(@RequestBody payload: UserDetailsDTO): UserDetailsDTO {
        val authorizedUser = SecurityContextHolder.getContext().authentication
        // Fields userId and role cannot be changed
        payload.userId = authorizedUser.principal.toString().toLong()
        payload.role = authorizedUser.authorities.toString()
        return userDetailsService.updateProfile(payload)
    }

    @GetMapping("/my/tickets")
    fun getTickets(): List<TicketPurchasedDTO> {
        val authorizedUser = SecurityContextHolder.getContext().authentication
        return ticketsService.getTickets(authorizedUser.principal.toString().toLong())
    }

    @GetMapping("/my/tickets/{ticketId}")
    fun getTicketQRCode(
        @PathVariable ticketId: UUID,
        @RequestHeader("Accept") accept: String
    ): ResponseEntity<Any> {

        val ticket = try {
            ticketsService.getTicket(ticketId).jws!!
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        return when (accept) {
            "image/*", "image/png" -> {

                val qr = QRCode(ticket).render(8, 20)
                val ba = ByteArrayOutputStream()
                    .also { qr.writeImage(it) }
                    .toByteArray()

                ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(ba)
            }

            else -> {

                val res = TicketResponseOk(ticket)
                ResponseEntity.status(HttpStatus.OK).body(res)

            }
        }
    }

    @GetMapping("/admin/travelers")
    fun getTravelersUsernames(): List<String> {
        return userDetailsService.getRegisteredUsernames()
    }

    @GetMapping("/admin/traveler/{userId}/profile")
    fun getProfileByUserId(@PathVariable userId: Long): ResponseEntity<UserDetailsDTO> {
        val profile = userDetailsService.getProfile(userId)
        return if (profile.isPresent)
            ResponseEntity.status(HttpStatus.OK).body(profile.get())
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @GetMapping("/admin/traveler/{userId}/tickets")
    fun getTicketsByUserId(@PathVariable userId: Long): List<TicketPurchasedDTO> {
        return ticketsService.getTickets(userId)
    }

}
