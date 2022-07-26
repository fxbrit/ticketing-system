package it.polito.wa2.ticketcatalogueservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table("orders")
data class Order(
    @Id
    val id: Long?,

    @Column("ticketid")
    val ticketId: Long,

    val quantity: Int,

    @Column("userid")
    val userId: Long,

    var status: String,

    val time: Timestamp,

    @org.springframework.data.annotation.Transient
    val ticket: Ticket?
)
