package it.polito.wa2.paymentservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.sql.Timestamp

data class Payment(

    @Id
    @Column("paymentid")
    val paymentId: Long?,

    @Column("orderid")
    val orderId: Long,

    @Column("userid")
    val userId: Long,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */
    @Column("status")
    var status: Int,

    val time: Timestamp

    )
