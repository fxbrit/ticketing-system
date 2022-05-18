package it.polito.wa2.paymentservice.services

import it.polito.wa2.paymentservice.entities.Payment
import it.polito.wa2.paymentservice.repositories.PaymentRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentService {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    fun getAllPayments(): Flow<Payment> {
        return paymentRepository.findAll()
    }

    suspend fun getAllPaymentsByUser(userId: Long) {
        return paymentRepository.findAllByUserId(userId)
    }

}
