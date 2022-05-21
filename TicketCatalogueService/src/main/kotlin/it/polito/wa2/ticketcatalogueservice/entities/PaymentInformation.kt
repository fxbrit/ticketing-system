package it.polito.wa2.ticketcatalogueservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("payments")
data class PaymentInformation(
    @Id
    val id: Long,
    val creditCardNumber: Int,
    val cvv: Int,
    val expirationDate: LocalDate,
    val userId: Long
)
