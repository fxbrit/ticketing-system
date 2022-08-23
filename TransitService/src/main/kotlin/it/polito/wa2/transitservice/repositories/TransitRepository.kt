package it.polito.wa2.transitservice.repositories

import it.polito.wa2.transitservice.entities.Transit
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface TransitRepository : CoroutineCrudRepository<Transit, Long> {}
