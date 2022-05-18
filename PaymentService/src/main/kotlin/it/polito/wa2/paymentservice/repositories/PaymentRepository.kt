package it.polito.wa2.paymentservice.repositories

import it.polito.wa2.paymentservice.entities.Payment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Repository
interface PaymentRepository : CoroutineCrudRepository<Payment, Long> {

    var client: DatabaseClient
    var operator: TransactionalOperator

    suspend fun findAllByUserId(userId: Long) {

        val query = ("""
           SELECT * 
           FROM payments p
           WHERE p.userid = $userId
        """)

        operator.executeAndAwait { client.sql(query).await() }

    }

}
