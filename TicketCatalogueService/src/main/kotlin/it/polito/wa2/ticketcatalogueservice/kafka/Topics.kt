package it.polito.wa2.ticketcatalogueservice.kafka
class Topics {
    companion object Constants {
        const val catalogueToPayment: String = "catalogueToPayment"
        const val paymentToCatalogue: String = "paymentToCatalogue"
    }
}
