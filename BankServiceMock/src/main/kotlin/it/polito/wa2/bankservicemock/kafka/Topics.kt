package it.polito.wa2.bankservicemock.kafka

class Topics {
    companion object Constants {
        const val paymentToBank: String = "paymentToBank"
        const val bankToPayment: String = "bankToPayment"
    }
}