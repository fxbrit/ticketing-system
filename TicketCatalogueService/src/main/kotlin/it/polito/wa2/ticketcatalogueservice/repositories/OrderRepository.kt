package it.polito.wa2.ticketcatalogueservice.repositories

import it.polito.wa2.ticketcatalogueservice.entities.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long> {

    @Query("""
       SELECT * 
       FROM orders o, tickets t, payments p, users u 
       WHERE o.userid = u.id 
       AND o.ticketid = t.id 
       AND o.paymentid = p.id
    """)
    fun findAllOrders(): Flow<Order>

    @Query("""
        SELECT * 
       FROM orders o, tickets t, payments p, users u 
       WHERE o.userid = u.id 
       AND o.ticketid = t.id 
       AND o.paymentid = p.id
       AND o.id = :id AND o.userid = :userId
    """)
     suspend fun findOrderById(@Param("id") id: Long, @Param("userId") userId: Long): Order?

     @Query("""
         SELECT *
         FROM orders o, tickets t, payments p, users u 
         WHERE o.userid = u.id
         AND o.ticketid = t.id 
         AND o.paymentid = p.id
         AND o.userid = :userid
     """)
     fun findUserOrders(@Param("userid") id: Long) : Flow<Order>
}
