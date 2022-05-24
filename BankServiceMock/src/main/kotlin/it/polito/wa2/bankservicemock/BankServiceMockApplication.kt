package it.polito.wa2.bankservicemock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankServiceMockApplication

fun main(args: Array<String>) {
    runApplication<BankServiceMockApplication>(*args)
}
