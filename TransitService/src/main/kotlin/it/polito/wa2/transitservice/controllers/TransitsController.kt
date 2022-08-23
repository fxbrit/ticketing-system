package it.polito.wa2.transitservice.controllers

import it.polito.wa2.transitservice.dto.TransitsPerTurnstileDTO
import it.polito.wa2.transitservice.services.TransitsService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TransitsController {

    @Autowired
    lateinit var transitsService: TransitsService

    @GetMapping("/transitsPerTurnstile")
    suspend fun transitsPerTurnstile(): Flow<TransitsPerTurnstileDTO> {
        return transitsService.transitsPerTurnstile()
    }
}
