package it.polito.wa2.paymentservice.repositories

import it.polito.wa2.paymentservice.entities.Payment
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : CoroutineCrudRepository<Payment, Long> {

    suspend fun findAllByUserId(userId: Long): Flow<Payment>

    @Query(
        """
       SELECT * 
       FROM payment p
       WHERE p.time >= CAST(:st as timestamp) AND p.time <= CAST(:end as timestamp)
    """
    )
    fun findAllPayments(
        @Param("st") startDate: String?,
        @Param("end") endDate: String?
    ): Flow<Payment>

    @Query(
        """
       SELECT * 
       FROM payment p
       WHERE p.paymentid = :id
       AND p.time >= CAST(:st as timestamp) AND p.time <= CAST(:end as timestamp)
    """
    )
    fun findAllPaymentsByUserID(
        @Param("id") userId: Long,
        @Param("st") startDate: String?,
        @Param("end") endDate: String?
    ): Flow<Payment>

}
