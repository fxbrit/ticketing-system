package it.polito.wa2.ticketcatalogueservice.Repositories

import it.polito.wa2.ticketcatalogueservice.Entities.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long>{

    @Query("""
       SELECT * 
       FROM orders o, tickets t, payments p, users u 
       WHERE o.userid = u.id 
       AND o.ticketid = t.id 
       AND o.paymentid = p.id
    """)
    fun findAllOrders(): Flow<Order>
}
