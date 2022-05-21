package it.polito.wa2.paymentservice.repositories

import it.polito.wa2.paymentservice.entities.Payment
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentRepository : CoroutineCrudRepository<Payment, UUID> {

    suspend fun findAllByUserId(userId: Long): Flow<Payment>

}
