package it.polito.wa2.paymentservice.controller

import it.polito.wa2.paymentservice.dtos.PaymentDTO
import it.polito.wa2.paymentservice.services.PaymentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController {

    @Autowired
    lateinit var paymentService: PaymentService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as Long }

    @GetMapping("/admin/transactions")
    fun getAllTransactions(
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Flow<PaymentDTO> {
        return paymentService.getAllPayments(startDate, endDate)
    }

    @GetMapping("/transactions")
    suspend fun getAllOrdersByUser(): Flow<PaymentDTO> {
        val userId = principal.awaitSingle()
        return paymentService.getAllPaymentsByUser(userId)
    }

    @GetMapping("/admin/transactions/{userId}")
    suspend fun getAllTransctionsbyUserAdmin(
        @PathVariable userId: Long,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?
    ): Flow<PaymentDTO> {
        return paymentService.getAllPaymentsByUserAdmin(userId, startDate, endDate)
    }

}
