package it.polito.wa2.paymentservice.kafka

class Topics {
    companion object Constants {
        const val paymentToBank: String = "paymentToBank"
        const val bankToPayment: String = "bankToPayment"
        const val travelerToPayment: String = "travelerToPayment"
        const val paymentToTraveler: String = "paymentToTraveler"
    }
}
