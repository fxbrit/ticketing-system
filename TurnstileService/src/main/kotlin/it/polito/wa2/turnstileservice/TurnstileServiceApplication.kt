package it.polito.wa2.turnstileservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TurnstileServiceApplication

fun main(args: Array<String>) {
    runApplication<TurnstileServiceApplication>(*args)
}
