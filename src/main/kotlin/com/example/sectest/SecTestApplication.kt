package com.example.sectest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SecTestApplication

fun main(args: Array<String>) {
    runApplication<SecTestApplication>(*args)
}
