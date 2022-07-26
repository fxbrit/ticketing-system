package it.polito.wa2.paymentservice.dtos

import it.polito.wa2.paymentservice.entities.Payment
import java.sql.Timestamp

data class PaymentDTO(
    val paymentId: Long?,
    val orderId: Long,
    val userId: Long,
    var status: Int,
    val time: Timestamp
    )

fun Payment.toDTO() = PaymentDTO(paymentId, orderId, userId, status, time)
