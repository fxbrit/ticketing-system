package it.polito.wa2.paymentservice.dtos

import it.polito.wa2.paymentservice.entities.Payment

data class PaymentDTO(
    val paymentId: Long?,
    val orderId: Long,
    val userId: Long,
    var status: Int
    )

fun Payment.toDTO() = PaymentDTO(paymentId, orderId, userId, status)
