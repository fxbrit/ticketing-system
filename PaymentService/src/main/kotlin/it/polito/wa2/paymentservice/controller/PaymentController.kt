package it.polito.wa2.paymentservice.controller

import it.polito.wa2.paymentservice.entities.Payment
import it.polito.wa2.paymentservice.services.PaymentService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController {

    @Autowired
    lateinit var paymentService: PaymentService

    @GetMapping("/admin/transactions")
    fun getAllOrders(): Flow<Payment> {
        /**
         * TODO: 18/05/22
         * add security and check that user is admin.
         */
        return paymentService.getAllPayments()
    }

    @GetMapping("/transactions")
    suspend fun getAllOrdersByUser(): Flow<Payment> {
        /**
         * TODO: 18/05/22
         * for now userId is stubbed with fixed value, it should be obtained
         * from the JWT instead.
         *
         * val authorizedUser = SecurityContextHolder.getContext().authentication
         * payload.userId = authorizedUser.principal.toString().toLong()
         */
        return paymentService.getAllPaymentsByUser(userId = 123)
    }

}