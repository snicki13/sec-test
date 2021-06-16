package com.example.sectest

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class Controller {

    @GetMapping("/")
    fun hello() = Mono.just("Hallo")

    @GetMapping("/sec")
    fun sec(auth: Authentication) = Mono.just(auth.name)
}
