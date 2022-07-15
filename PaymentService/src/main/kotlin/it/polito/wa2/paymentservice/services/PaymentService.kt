package it.polito.wa2.paymentservice.services

import it.polito.wa2.paymentservice.dtos.PaymentDTO
import it.polito.wa2.paymentservice.dtos.toDTO
import it.polito.wa2.paymentservice.repositories.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentService {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    fun getAllPayments(): Flow<PaymentDTO> {
        return paymentRepository.findAll().map { it.toDTO() }
    }

    suspend fun getAllPaymentsByUser(userId: Long): Flow<PaymentDTO> {
        return paymentRepository.findAllByUserId(userId).map { it.toDTO() }
    }

}
