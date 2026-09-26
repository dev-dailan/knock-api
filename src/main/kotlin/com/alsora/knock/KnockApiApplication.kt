package com.alsora.knock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KnockApiApplication

fun main(args: Array<String>) {
    runApplication<KnockApiApplication>(*args)
}
