package com.example.sectest

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class UserController(val authenticationManager: ReactiveAuthenticationManager,
                    val securityContextRepository: ServerSecurityContextRepository) {

    @PostMapping("/signin")
    fun signIn(@RequestBody signInForm: SignInForm, webExchange: ServerWebExchange?): Mono<Void>? {
        return Mono.just<SignInForm>(signInForm)
            .flatMap { form: SignInForm ->
                val token = UsernamePasswordAuthenticationToken(
                    form.username,
                    form.password
                )
                authenticationManager
                    .authenticate(token)
                    .doOnError { err -> System.out.println(err.message) }
                    .flatMap { authentication ->
                        val securityContext = SecurityContextImpl(authentication)
                        securityContextRepository
                            .save(webExchange, securityContext)
                            .subscriberContext(
                                ReactiveSecurityContextHolder.withSecurityContext(
                                    Mono.just(
                                        securityContext
                                    )
                                )
                            )
                    }
            }
    }
}

data class SignInForm(val username: String, val password: String)
