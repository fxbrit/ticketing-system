package it.polito.wa2.turnstileservice.repositories

import it.polito.wa2.turnstileservice.entities.Turnstile
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TurnstileRepository : CoroutineCrudRepository<Turnstile, Long> {

    @Query("""
       SELECT * 
       FROM turnstiles t
       WHERE t.username = :username
    """)
    suspend fun findTurnstileByUsername(@Param("username") username: String): Turnstile?

}
