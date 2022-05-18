package it.polito.wa2.paymentservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column

data class Payment(

    @Id
    @Column("paymentid")
    val paymentId: Long,

    @Column("userid")
    val userId: Long,

    @Column("status")
    val status: Int

)
