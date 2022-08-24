package it.polito.wa2.transitservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.Date

@Table("transits")
data class Transit(

    @Id
    @Column("id")
    val id: Long?,

    @Column("ticketUUID")
    val ticketUUID: String,

    @Column("userID")
    val userID: Long,

    @Column("turnstileID")
    val turnstileID: Long,

    @Column("time")
    val time: Date

)
