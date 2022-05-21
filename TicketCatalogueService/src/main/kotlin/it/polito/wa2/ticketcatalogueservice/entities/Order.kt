package it.polito.wa2.ticketcatalogueservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
        @Id
    val id: Long,

        @Column("ticketid")
    val ticketId: Long,

        val quantity: Int,

        @Column("paymentid")
    val paymentId: Long,

        @Column("userid")
    val userId: Long,

        @org.springframework.data.annotation.Transient
    val ticket: Ticket,

        @org.springframework.data.annotation.Transient
    val user: User,

        @org.springframework.data.annotation.Transient
    val paymentInformation: PaymentInformation

)
